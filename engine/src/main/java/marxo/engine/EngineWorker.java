package marxo.engine;

import marxo.entity.MongoDbAware;
import marxo.entity.Task;
import marxo.entity.action.Action;
import marxo.entity.action.MonitorableAction;
import marxo.entity.link.Link;
import marxo.entity.node.Event;
import marxo.entity.node.Node;
import marxo.entity.workflow.RunStatus;
import marxo.entity.workflow.Workflow;
import marxo.tool.Loggable;
import marxo.tool.StringTool;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.*;

public class EngineWorker implements Runnable, MongoDbAware, Loggable {
	String name;
	public boolean isStopped = false;

	public EngineWorker() {
		this(UUID.randomUUID().toString());
	}

	public EngineWorker(String name) {
		this.name = name;
	}

	// todo: make the method thread-safe, ready for multi-thread.
	@Override
	public void run() {
		logger.debug(String.format("%s starts", this));

		Task task = null;
		Workflow workflow = null;

		try {
			task = Task.next();

			if (task == null) {
				logger.debug(String.format("%s finds no task", this));
				return;
			}

			workflow = Workflow.get(task.workflowId);

			if (workflow == null) {
				logger.warn(String.format("%s Cannot find workflow %s", task, task.workflowId));
				return;
			}

			if (workflow.startNodeId == null) {
				logger.debug(String.format("%s has no start node", this));
				workflow.status = RunStatus.ERROR;
				workflow.save();
				return;
			}

			if (workflow.getCurrentNodes().isEmpty()) {
				if (workflow.nodeIds.isEmpty()) {
					logger.warn(String.format("%s has no node", workflow));
					return;
				}
				workflow.addCurrentNode(workflow.getStartNode());
			}

			Queue<Node> nodeQueue = new LinkedList<>(workflow.getCurrentNodes());
			List<ObjectId> pendingNodeIds = new ArrayList<>();
			boolean isScheduled = false;

			while (!nodeQueue.isEmpty()) {
				Node node = nodeQueue.poll();
				logger.debug(String.format("%s is processing %s", this, node));

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
					logger.debug(String.format("%s is processing %s", this, action));

					Event event = action.getEvent();
					if (event == null || event.getStartTime() == null || event.getStartTime().isBeforeNow()) {
						if (event == null) {
							event = new Event();
						}

						if (event.getStartTime() == null) {
							event.setStartTime(DateTime.now());
							event.save();
						}

						boolean isOkay = action.act();

						if (isOkay) {
							if (action instanceof MonitorableAction) {
								MonitorableAction monitorableAction = (MonitorableAction) action;
								if (monitorableAction.isTracked) {
									workflow.addTracableAction(monitorableAction);
								}
								action.status = RunStatus.MONITORING;
							} else {
								action.status = RunStatus.FINISHED;
							}
						} else {
							logger.error(String.format("%s encounters errors: %s", this, action.getErrors()));
							node.save();
							break;
						}
					} else {
						reschedule(workflow.id, event.getStartTime());
						isScheduled = true;
						pendingNodeIds.add(node.id);
						break;
					}
				}

				if (action == null) {// if all actions have been run
					node.status = RunStatus.FINISHED;
				}

				node.save();

				// Check links
				for (Link link : node.getToLinks()) {
					logger.debug(String.format("%s is processing %s", this, link));
					if (link.determine()) {
						link.status = RunStatus.FINISHED;
						link.save();

						if (link.getNextNode() != null) {
							nodeQueue.add(link.getNextNode());
						}
					} else {
						reschedule(workflow.id, DateTime.now());
						isScheduled = true;
					}
				}
			}

			if (pendingNodeIds.isEmpty()) {
				if (workflow.tracableActionIds.isEmpty()) {
					workflow.status = RunStatus.FINISHED;
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
			}
		}
	}

	public Task reschedule(ObjectId workdflowId, DateTime time) {
		Criteria workflowIdCriteria = Criteria.where("workflowId").is(workdflowId);
		Update update = Update.update("time", time).set("workflowId", workdflowId);
		Task task;

		if (mongoTemplate.exists(Query.query(workflowIdCriteria), Task.class)) {
			task = mongoTemplate.findAndModify(Query.query(workflowIdCriteria.and("time").gt(time)), update, Task.class);
		} else {
			task = new Task(workdflowId);
			task.time = time;
			task.save();
		}

		if (task == null) {
			logger.debug(String.format("%s skips reschedule due to earlier task is found", this));
		} else {
			logger.debug(String.format("%s schedule a task [%s] on %s", this, workdflowId, time));
		}

		return task;
	}
}
