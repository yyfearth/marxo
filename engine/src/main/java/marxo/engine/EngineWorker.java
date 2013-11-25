package marxo.engine;

import com.google.common.base.Predicate;
import marxo.entity.Task;
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

				{// Process action
					if (node.firstAction() == null) {
						continue;
					}

					Action currentAction = node.getCurrentAction();
					if (currentAction == null) {
						currentAction = node.firstAction();
					}

					while (true) {
						if (currentAction.status.equals(RunStatus.FINISHED)) {
							currentAction = currentAction.getNextAction();

							if (currentAction == null) {
								// Do next node.
								break;
							}

							continue;
						}

						// Check event
						Event event = currentAction.getEvent();
						if (event == null || event.getStartTime().isBeforeNow()) {
							currentAction.act();
							currentAction.status = RunStatus.FINISHED;
							// update the action
						} else {
							// put a task into queue
							Task newTask = new Task(workflow.id);
							task.save();
							break;
						}
					}
				}
			}

//			Action action = null;
//			while (action == null) {
//				if (workflow.currentActionIds.size() == 0) {
//					Node node = mongoTemplate.findById(workflow.startNodeId, Node.class);
//					// todo: let node is tracable from action.
//
//				}
//
//				if (action.getEvent() == null) {
//					action.act();
//				} else {
//					if (action.getEvent().getStartTime() == null || action.getEvent().getStartTime().isBeforeNow()) {
//						// do it immediately
//					} else {
//						// add another task to the dao
//					}
//				}
//			}

//			if (workflow.currentActionIds.size() == 0) {
//				workflow.currentActionIds.add()
//			}

//			Node node = nodeDao.findOne(workflow.currentNodeId);
//
//			for (Action action : node.actions) {
//				action.act();
//			}
//
//			final Workflow w = workflow;
//
//			Action action = Iterables.find(node.actions, new Predicate<Action>() {
//				@Override
//				public boolean apply(Action input) {
//					return input.id.equals(w.currentActionId);
//				}
//			});

			// todo: do if the schedule hits

			workflow.status = RunStatus.FINISHED;
			workflow.save();
		} catch (Exception e) {
			logger.error("[" + name + "] " + e.getMessage());
			logger.error(StringTool.exceptionToString(e));
		}
	}
}
