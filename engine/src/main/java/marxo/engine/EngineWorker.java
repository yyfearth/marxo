package marxo.engine;

import marxo.entity.MongoDbAware;
import marxo.entity.Task;
import marxo.entity.action.Action;
import marxo.entity.action.TrackableAction;
import marxo.entity.link.Link;
import marxo.entity.node.Event;
import marxo.entity.node.Node;
import marxo.entity.workflow.Notification;
import marxo.entity.workflow.RunStatus;
import marxo.entity.workflow.Workflow;
import marxo.tool.Loggable;
import marxo.tool.StringTool;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Duration;
import org.joda.time.Seconds;

import java.util.UUID;

public class EngineWorker implements Runnable, MongoDbAware, Loggable {

	String name;

	public EngineWorker() {
		this(UUID.randomUUID().toString());
	}

	public EngineWorker(String name) {
		this.name = name;
	}

	/*
	Concurrent control
	 */

	public static Thread thread;
	public static EngineWorker engineWorker;
	public boolean isStopped = false;

	public static void startAsync() {
		engineWorker = new EngineWorker("Singleton");
		thread = new Thread(engineWorker);
		thread.start();
	}

	public static void stop() {
		stopAsync();

		try {
			if (thread != null) {
				thread.join(Seconds.seconds(10).toStandardDuration().getMillis());
			}
		} catch (InterruptedException e) {
			logger.error(String.format("%s got error [%s] %s", engineWorker, e.getClass().getSimpleName(), e.getMessage()));
		} finally {
			thread = null;
			engineWorker = null;
		}
	}

	public static void stopAsync() {
		if (engineWorker != null) {
			engineWorker.isStopped = true;
		}
	}

	public static boolean isAlive() {
		return thread != null && thread.isAlive();
	}

	/*
	Worker
	 */

	final Duration idleDuration = Seconds.seconds(3).toStandardDuration();
	final Duration normalDuration = Seconds.seconds(1).toStandardDuration();
	/**
	 * The duration since last time getting a task.
	 */
	Duration duration = idleDuration;
	/**
	 * The time to get a task.
	 */
	DateTime nextRunTime = DateTime.now();
	/**
	 * The duration between thread stopping check.
	 */
	Duration spinDuration = Seconds.seconds(1).toStandardDuration();

	/**
	 * If you do not fully understand the application, or not pointed by a gun to do this, you probably don't want to touch this method. <b>IT DOESN'T BELONG TO HUMAN WORLD!</b>
	 */
	@Override
	public void run() {
		logger.info(String.format("%s starts", this));

		Task task = null;
		Workflow workflow = null;

		try {
			while (!isStopped) {

				if (nextRunTime.isAfterNow()) {
					Thread.sleep(spinDuration.getMillis());
					continue;
				}

				task = Task.next();

				if (task == null) {
					nextRunTime = DateTime.now().plus(idleDuration);
					continue;
				}

				workflow = Workflow.get(task.workflowId);

				if (workflow == null) {
					logger.warn(String.format("%s Cannot find workflow %s", task, task.workflowId));
					continue;
				}

				try {

					if (workflow.isTracked()) {
						processTrackableActions(workflow);
					}

//					if (!makeSureCurrentNode(workflow)) {
//						continue;
//					}

					boolean isOkay;
					while (true) {
						isOkay = processNodes(workflow);

						if (linksAreUpdated) {
							processLinks(workflow);
						}

						if (nodesAreUpdated) {
							continue;
						}

						break;
					}

					if (!isOkay) {
						logger.info(String.format("%s has error", workflow));
						workflow.setStatus(RunStatus.ERROR);
					} else if (workflow.isDone()) {
						logger.info(String.format("%s finishes", workflow));
						workflow.setStatus(RunStatus.FINISHED);
						Notification.saveNew(Notification.Level.NORMAL, workflow, Notification.Type.FINISHED);
					} else {
						logger.info(String.format("%s tracks", workflow));
						workflow.setStatus(RunStatus.TRACKED);
					}
				} catch (Exception e) {
					logger.error(String.format("%s has error:", this));
					logger.error(StringTool.exceptionToString(e));
					workflow.setStatus(RunStatus.ERROR);
				} finally {
					workflow.save();
					// Schedule the task in the end of the whole process to prevent more than one worker takes the same workflow.
					scheduleTask(workflow.id);
					nextRunTime = DateTime.now().plus(normalDuration);
				}
			}

			logger.info(String.format("%s ends", this));
		} catch (InterruptedException ignored) {
		}
	}

	boolean nodesAreUpdated = false;

	private void processLinks(Workflow workflow) {
		nodesAreUpdated = false;

		for (int i = 0; i < workflow.getCurrentLinks().size(); i++) {
			Link link = workflow.getCurrentLinks().get(i);
			logger.info(String.format("%s starts", link));
			Notification.saveNew(Notification.Level.NORMAL, link, Notification.Type.STARTED);

			if (link.determine()) {
				logger.info(String.format("%s finishes", link));
				workflow.removeCurrentLink(link);
				i--;
				link.setStatus(RunStatus.FINISHED);
				link.save();

				Notification.saveNew(Notification.Level.NORMAL, link, Notification.Type.FINISHED);

				if (link.getNextNode() != null) {
					workflow.addCurrentNode(link.getNextNode());
					nodesAreUpdated = true;
				}
			} else {
				updateSchedule(DateTime.now().plus(Days.ONE.toStandardDuration()));
			}
		}
	}

	boolean linksAreUpdated = false;

	private boolean processNodes(Workflow workflow) {
		linksAreUpdated = false;
		boolean hasError = false;

		for (int i = 0; i < workflow.getCurrentNodes().size(); i++) {
			Node node = workflow.getCurrentNodes().get(i);

			if (node.getStatus().equals(RunStatus.IDLE)) {
				logger.info(String.format("%s starts", node));
				node.setStatus(RunStatus.STARTED);
				Notification.saveNew(Notification.Level.NORMAL, node, Notification.Type.STARTED);
			} else if (node.isNot(RunStatus.FINISHED) && node.isNot(RunStatus.STARTED)) {
				continue;
			}

			Action lastAction = null;
			for (Action action : node.getActions()) {
				lastAction = action;

				Event event = action.getEvent();
				if (event == null) {
					event = new Event();
					event.setStartTime(DateTime.now());
					event.setEndTime(DateTime.now());
					action.setEvent(event);
				} else if (event.getStartTime() == null) {
					event.setStartTime(DateTime.now());
				}

				if (action.getStatus().equals(RunStatus.IDLE)) {
					if (event.getStartTime().isAfterNow()) {
						updateSchedule(event.getStartTime());
						node.setCurrentAction(action);
						break;
					} else {
						Notification.saveNew(Notification.Level.NORMAL, action, Notification.Type.STARTED);

						action.act(workflow, node);
						action.save();
						logger.info(String.format("%s acts, after-status: [%s]", action, action.getStatus()));
					}
				}

				if (action.getStatus().equals(RunStatus.STARTED)) {
					if (event.getEndTime().isAfterNow()) {
						updateSchedule(event.getEndTime());
						node.setCurrentAction(action);
						break;
					} else {
						logger.info(String.format("%s finishes", action));
						action.setStatus(RunStatus.FINISHED);
						Notification.saveNew(Notification.Level.NORMAL, action, Notification.Type.FINISHED);
						continue;
					}
				}

				if (action.isTracked() || action.isFinished()) {
					continue;
				}

				break;
			}

			if (lastAction == null) {
				logger.info(String.format("%s finishes", node));
				node.setStatus(RunStatus.FINISHED);
			} else {
				logger.info(String.format("%s %s", node, lastAction.getStatus().toString().toLowerCase()));
				node.setStatus(lastAction.getStatus());
			}

			node.save();    // Must save this because current workflow has no node information.

			if (node.isFinished()) {
				Notification.saveNew(Notification.Level.NORMAL, node, Notification.Type.FINISHED);
				workflow.removeCurrentNode(node);
				i--;
				workflow.addCurrentLinks(node.getToLinks());
				linksAreUpdated = true;
			} else if (node.isTracked()) {
				Notification.saveNew(Notification.Level.NORMAL, node, Notification.Type.TRACKED);
				workflow.removeCurrentNode(node);
				i--;
				workflow.addCurrentLinks(node.getToLinks());
				linksAreUpdated = true;
			} else {
				if (node.getStatus().equals(RunStatus.ERROR)) {
					hasError = true;
				}
			}
		}

		return !hasError;
	}

	private boolean makeSureCurrentNode(Workflow workflow) {
		if (workflow.getCurrentNodes().isEmpty()) {

			if (workflow.getStartNode() == null) {
				workflow.getNodes();

				if (workflow.getNodes().isEmpty()) {
					logger.warn(String.format("%s has no node", workflow));
					Notification.saveNew(Notification.Level.ERROR, workflow, Notification.Type.ERROR);
					workflow.setStatus(RunStatus.ERROR);
					return false;
				}

				workflow.getLinks();
				workflow.wire();
			}

			workflow.addCurrentNode(workflow.getStartNode());
		}
		return true;
	}

	DateTime scheduleTime = null;

	void updateSchedule(DateTime time) {
		if (scheduleTime == null) {
			scheduleTime = time;
			return;
		}

		if (scheduleTime.isBefore(time)) {
			return;
		}

		scheduleTime = time;
	}

	void scheduleTask(ObjectId workflowId) {
		if (scheduleTime == null) {
			return;
		}

		Task.schedule(workflowId, scheduleTime);
		scheduleTime = null;
	}

	void processTrackableActions(Workflow workflow) {
		for (int i = 0; i < workflow.getTrackedActions().size(); i++) {
			TrackableAction trackableAction = workflow.getTrackedActions().get(i);

			try {
				Node node = trackableAction.getNode();
				trackableAction.act(workflow, node);
				trackableAction.save();

				if (trackableAction.isFinished()) {
					workflow.removeTracableAction(trackableAction);
					i--;

					boolean isFinished = true;
					for (Action action : node.getActions()) {
						if (action.isNot(RunStatus.FINISHED)) {
							isFinished = false;
							break;
						}
					}
					if (isFinished) {
						logger.info(String.format("%s finishes tracking", node));
						node.setStatus(RunStatus.FINISHED);
						node.save();
						workflow.removeCurrentNode(node);

						if (workflow.getCurrentNodes().size() == 0) {
							logger.info(String.format("%s finishes tracking", workflow));
							workflow.setStatus(RunStatus.FINISHED);
							Notification.saveNew(Notification.Level.NORMAL, workflow, Notification.Type.FINISHED);
						}
					}
				}
			} catch (Exception e) {
				logger.error(String.format("%s has error when tracking [%s]", this, e));
				trackableAction.getNode().setStatus(RunStatus.ERROR);
				trackableAction.setStatus(RunStatus.ERROR);
				throw e;
			}
		}
	}

	@Override
	public String toString() {
		return String.format("Worker[%s]", name);
	}
}
