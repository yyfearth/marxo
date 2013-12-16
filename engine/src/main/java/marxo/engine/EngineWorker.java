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
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Seconds;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
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

	final Duration idleDuration = Seconds.seconds(1).toStandardDuration();
	final Duration normalDuration = Seconds.seconds(1).toStandardDuration();
	Duration duration = idleDuration;
	Duration spinDuration = Seconds.seconds(1).toStandardDuration();
	DateTime nextCheckTime = DateTime.now();

	public void setDuration(Duration duration) {
		if (!this.duration.equals(duration)) {
			this.duration = duration;
		}
	}

	// todo: make the method thread-safe, ready for multi-thread.
	@Override
	public void run() {
		logger.info(String.format("%s starts", this));

		Task task = null;
		Workflow workflow = null;

		try {
			doWorkflow:
			while (true) {
				if (isStopped) {
					break;
				}

				if (nextCheckTime.isAfterNow()) {
					Thread.sleep(spinDuration.getMillis());
					continue;
				}

				try {
					task = Task.next();

					if (task == null) {
						setDuration(idleDuration);
						continue;
					} else {
						setDuration(normalDuration);
					}

					workflow = Workflow.get(task.workflowId);

					if (workflow == null) {
						logger.warn(String.format("%s Cannot find workflow %s", task, task.workflowId));
						continue;
					}

					logger.info(String.format("%s is processing %s", this, workflow));

					if (workflow.startNodeId == null) {
						logger.warn(String.format("%s has no start node", this));

						Notification.saveNew(Notification.Level.ERROR, workflow, "Workflow has no start node");

						workflow.setStatus(RunStatus.ERROR);
						workflow.save();
						continue;
					}

					if (workflow.getCurrentNodes().isEmpty()) {
						if (workflow.nodeIds.isEmpty()) {
							logger.warn(String.format("%s has no node", workflow));
							continue;
						}
						workflow.addCurrentNode(workflow.getStartNode());
						workflow.save();
					}

					Queue<Node> nodeQueue = new LinkedList<>(workflow.getCurrentNodes());
					boolean workflowHasError = false;

					doNode:
					while (!nodeQueue.isEmpty()) {
						Node node = nodeQueue.poll();
						node.setWorkflow(workflow);
						logger.info(String.format("%s is processing %s", this, node));

						switch (node.getStatus()) {
							case IDLE:
								node.setStatus(RunStatus.STARTED);
								node.save();

								Notification.saveNew(Notification.Level.NORMAL, node, "Node started");
							case STARTED:
								break;
							case FINISHED:
							case PAUSED:
							case STOPPED:
							case ERROR:
							case WAITING:
							case TRACKED:
								continue doNode;
						}

						List<Action> actions = node.getActions();
						boolean nodeHasError = false;
						boolean nodeIsTracking = false;

						doAction:
						for (Action action : actions) {
							switch (action.getStatus()) {
								case IDLE:
								case STARTED:
									break;
								case WAITING:
								case FINISHED:
									break doAction;
								case TRACKED:
									nodeIsTracking = true;
									break;
								case ERROR:
									nodeHasError = true;
								case PAUSED:
								case STOPPED:
									break doAction;
							}

							Event event = action.getEvent();
							if (event == null) {
								event = new Event();
								event.setStartTime(DateTime.now());

								action.setEvent(event);
								action.save();
							} else if (event.getStartTime() == null) {
								event.setStartTime(DateTime.now());
								event.save();
							}

							if (event.getStartTime().isAfterNow()) {
								Task.reschedule(workflow.id, event.getStartTime());
							}

							Notification.saveNew(Notification.Level.NORMAL, action, "Action started");

							action.act();
							action.save();

							switch (action.getStatus()) {
								case FINISHED:
									Notification.saveNew(Notification.Level.NORMAL, action, "Action finished");
									break;
								case TRACKED:
									nodeIsTracking = true;
									Notification.saveNew(Notification.Level.NORMAL, action, "Action tracked");
									workflow.addTracableAction((TrackableAction) action);
									workflow.save();
									break;
								case ERROR:
									nodeHasError = true;
								case IDLE:
								case STARTED:
								case WAITING:
								case PAUSED:
								case STOPPED:
									break doAction;
							}
						}

						if (nodeHasError) {
							node.setStatus(RunStatus.ERROR);
						} else if (nodeIsTracking) {
							node.setStatus(RunStatus.TRACKED);
						} else {
							node.setStatus(RunStatus.FINISHED);
//							workflow.getCurrentNodes().remove()
						}
						node.save();

						if (node.getStatus().equals(RunStatus.FINISHED) || node.getStatus().equals(RunStatus.TRACKED)) {
							Notification.saveNew(Notification.Level.NORMAL, node, "Node finished");
						} else {
							if (node.getStatus().equals(RunStatus.ERROR)) {
								workflowHasError = true;
							}
							continue;
						}

						// Run links
						for (Link link : node.getToLinks()) {
							logger.info(String.format("%s is processing %s", this, link));
							Notification.saveNew(Notification.Level.NORMAL, link, "Link started");

							if (link.determine()) {
								link.setStatus(RunStatus.FINISHED);
								link.save();

								Notification.saveNew(Notification.Level.NORMAL, link, "Link finished");

								if (link.getNextNode() != null) {
									nodeQueue.add(link.getNextNode());
									workflow.addCurrentNode(node);
									workflow.save();
								}
							} else {
								Task.reschedule(workflow.id, DateTime.now());
							}
						}
					}

					if (workflowHasError) {
						workflow.setStatus(RunStatus.ERROR);
					} else if (workflow.trackedActionIds.isEmpty()) {
						workflow.setStatus(RunStatus.FINISHED);
						Notification.saveNew(Notification.Level.NORMAL, workflow, String.format("Workflow %s finished", workflow.getName()));
					} else {
						workflow.setStatus(RunStatus.TRACKED);
					}
				} catch (Exception e) {
					logger.error(String.format("%s has error:", this));
					logger.error(StringTool.exceptionToString(e));
				} finally {
					if (workflow != null) {
						workflow.save();
						workflow = null;
					}
				}
			}
		} catch (InterruptedException e) {
			logger.info(String.format("%s is interruppted [%s]", this, e.getClass().getSimpleName()));
		}

		logger.info(String.format("%s ends", this));
	}

	protected void update() {

	}

	@Override
	public String toString() {
		return String.format("Worker[%s]", name);
	}
}
