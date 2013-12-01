package marxo.engine;

import marxo.entity.Task;
import marxo.entity.link.Link;
import marxo.entity.action.Action;
import marxo.entity.node.Event;
import marxo.entity.node.Node;
import marxo.entity.workflow.RunStatus;
import marxo.entity.workflow.Workflow;
import marxo.tool.Loggable;
import marxo.tool.StringTool;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

public class EngineWorker implements Runnable, Loggable {
	protected static final ApplicationContext applicationContext = new ClassPathXmlApplicationContext("mongo-configuration.xml");
	protected static final MongoTemplate mongoTemplate = applicationContext.getBean(MongoTemplate.class);
	String name;

	public EngineWorker() {
		this(UUID.randomUUID().toString());
	}

	public EngineWorker(String name) {
		this.name = name;
	}

	// todo: make the method thread-safe, ready for multi-thread.
	@Override
	public void run() {
		try {
			Task task = Task.next();

			if (task == null) {
				return;
			}

			Workflow workflow = Workflow.get(task.workflowId);

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
			boolean isDone = true;

			while (!nodeQueue.isEmpty()) {
				Node node = nodeQueue.poll();

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
						String message = String.format("%s shouldn't have %s status", node, node.status);
						logger.error(message);
						throw new IllegalStateException(message);
				}

				Action action = node.getCurrentAction();
				for (; action != null; action = action.getNextAction()) {
					Event event = action.getEvent();
					if (event == null || event.getStartTime().isBeforeNow()) {
						boolean isOkay = action.act();
						if (!isOkay) {
							logger.error(String.format("%s encounters errors: %s", this, action.getErrors()));
							node.save();
							break;
						}
					} else {
						Task newTask = new Task(workflow.id);
						newTask.time = event.getStartTime();
						task.save();
						break;
					}
				}

				if (action == null) {// if all actions have been run
					node.status = RunStatus.FINISHED;
					node.save();
				}

				// Check links
				for (Link link : node.getToLinks()) {
					// todo: add more currentNodes inside workflows.
				}
			}

			workflow.status = RunStatus.FINISHED;
			workflow.save();
		} catch (Exception e) {
			logger.error(String.format("%s has error: %s", this, e.getMessage()));
			logger.error(StringTool.exceptionToString(e));
		}
	}
}
