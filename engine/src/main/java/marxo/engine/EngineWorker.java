package marxo.engine;

import marxo.entity.MongoDbAware;
import marxo.entity.Task;
import marxo.entity.action.Action;
import marxo.entity.link.Link;
import marxo.entity.node.Node;
import marxo.entity.workflow.Notification;
import marxo.entity.workflow.RunStatus;
import marxo.entity.workflow.Workflow;
import marxo.tool.Loggable;
import marxo.tool.StringTool;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Seconds;

import java.util.*;

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

	final Duration idleDuration = Seconds.seconds(10).toStandardDuration();
	final Duration normalDuration = Seconds.seconds(1).toStandardDuration();
	Duration duration = idleDuration;

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
			for (; !isStopped; Thread.sleep(duration.getMillis())) {
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
						logger.info(String.format("%s has no start node", this));
						workflow.status = RunStatus.ERROR;
						workflow.save();
						continue;
					}

					if (workflow.getCurrentNodes().isEmpty()) {
						if (workflow.nodeIds.isEmpty()) {
							logger.warn(String.format("%s has no node", workflow));
							continue;
						}
						workflow.addCurrentNode(workflow.getStartNode());
					}

					Queue<Node> nodeQueue = new LinkedList<>(workflow.getCurrentNodes());
					List<ObjectId> pendingNodeIds = new ArrayList<>();
					boolean isScheduled = false;

					while (!nodeQueue.isEmpty()) {
						Node node = nodeQueue.poll();
						node.setWorkflow(workflow);
						logger.info(String.format("%s is processing %s", this, node));

						switch (node.status) {
							case STARTED:
							case IDLE:
								break;
							case FINISHED:
								continue;
							case PAUSED:
							case STOPPED:
							case ERROR:
							case WAITING:
							case MONITORING:
								String message = String.format("%s shouldn't have %s status", node, node.status);
								logger.error(message);
								throw new IllegalStateException(message);
						}

						Action action = node.getCurrentAction();
						for (; action != null; action = action.getNextAction()) {
							logger.info(String.format("%s is processing %s", this, action));

							boolean shouldContinue = action.act();

							if (!shouldContinue) {
								break;
							}
						}

						if (action == null) {// if all actions have been run
							node.status = RunStatus.FINISHED;

							Notification notification = new Notification(Notification.Level.NORMAL, String.format("Node %s finished", node.getName()));
							notification.setNode(node);
							notification.save();
						}

						node.save();

						// Check links
						for (Link link : node.getToLinks()) {
							logger.info(String.format("%s is processing %s", this, link));

							if (link.determine()) {
								link.status = RunStatus.FINISHED;
								link.save();

								Notification notification = new Notification(Notification.Level.NORMAL, String.format("Link %s finished", link.getName()));
								notification.setLink(link);
								notification.save();

								if (link.getNextNode() != null) {
									nodeQueue.add(link.getNextNode());
								}
							} else {
								Task.reschedule(workflow.id, DateTime.now());
								isScheduled = true;
							}
						}
					}

					workflow = Workflow.get(workflow.id);
					if (pendingNodeIds.isEmpty()) {
						if (workflow.tracedActionIds.isEmpty()) {
							workflow.status = RunStatus.FINISHED;

							Notification notification = new Notification(Notification.Level.NORMAL, String.format("Workflow %s finished", workflow.getName()));
							notification.setWorkflow(workflow);
							notification.save();
						} else {
							workflow.status = RunStatus.MONITORING;
						}
					} else {
						workflow.currentNodeIds = pendingNodeIds;
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

	@Override
	public String toString() {
		return String.format("Worker[%s]", name);
	}
}
