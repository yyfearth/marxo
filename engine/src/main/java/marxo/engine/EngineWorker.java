package marxo.engine;

import marxo.entity.Task;
import marxo.entity.link.Link;
import marxo.entity.node.Action;
import marxo.entity.node.Event;
import marxo.entity.node.Node;
import marxo.entity.workflow.RunStatus;
import marxo.entity.workflow.Workflow;
import marxo.tool.Loggable;
import marxo.tool.StringTool;
import org.bson.types.ObjectId;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;

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
			Task task = Task.findAndRemove();

			if (task == null) {
				return;
			}

			Workflow workflow = Workflow.get(task.workflowId);

			if (workflow == null) {
				logger.debug(String.format("Cannot find workflow [%s]", task.workflowId));
				return;
			}

			if (workflow.startNodeId == null) {
				logger.debug(String.format("Workflow [%s] has no start node", workflow.id));
				workflow.status = RunStatus.ERROR;
				workflow.save();
				return;
			}

			if (workflow.currentNodeIds.isEmpty()) {
				if (!workflow.nodeIds.isEmpty()) {
					workflow.currentNodeIds.add(workflow.startNodeId);
				}
			}

			for (int nodeIndex = 0; nodeIndex < workflow.currentNodeIds.size(); nodeIndex++) {
				ObjectId nodeId = workflow.currentNodeIds.get(nodeIndex);
				Node node = Node.get(nodeId);

				Node currentNode = node;

				while (currentNode != null) {
					Action currentAction = currentNode.getCurrentAction();
					boolean isScheduled = false;

					while (currentAction != null) {
						if (currentAction.status.equals(RunStatus.FINISHED)) {
							currentAction = currentAction.getNextAction();
							continue;
						}

						Event event = currentAction.getEvent();
						if (event == null || event.getStartTime().isBeforeNow()) {
							boolean isOkay = currentAction.act();
							if (isOkay) {
								currentAction.status = RunStatus.FINISHED;
								currentAction = currentAction.getNextAction();
								node.save();
							} else {
								currentAction.status = RunStatus.ERROR;
								node.save();
								break;
							}
						} else {
							// put a task into queue
							Task newTask = new Task(workflow.id);
							newTask.time = event.getStartTime();
							task.save();
							isScheduled = true;
							break;
						}
					}

					if (isScheduled) {
						break;
					}

					currentNode.status = RunStatus.FINISHED;
					currentNode.save();

					for (Link link : currentNode.getToLinks()) {
						// todo: add more currentNodes inside workflows.
					}

					currentNode = null;
				}
			}

			workflow.status = RunStatus.FINISHED;
			workflow.save();
		} catch (Exception e) {
			logger.error(String.format("Engine [%s] has error: %s", name, e.getMessage()));
			logger.error(StringTool.exceptionToString(e));
		}
	}
}
