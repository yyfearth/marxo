package marxo.engine;

import marxo.entity.Task;
import marxo.entity.node.Action;
import marxo.entity.node.Event;
import marxo.entity.node.Node;
import marxo.entity.workflow.Workflow;
import marxo.tool.Loggable;
import marxo.tool.StringTool;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class EngineWorker implements Runnable, Loggable {
	protected static final ApplicationContext applicationContext = new ClassPathXmlApplicationContext("mongo-configuration.xml");
	protected static final MongoTemplate mongoTemplate = applicationContext.getBean(MongoTemplate.class);
	String name;

	public EngineWorker(String name) {
		this.name = name;
	}

	// todo: make the method thread-safe, ready for multi-thread.
	@Override
	public void run() {
		try {

			Criteria criteria = new Criteria();
			Task task = mongoTemplate.findOne(Query.query(criteria), Task.class);

			if (task == null) {
				return;
			}

			Workflow workflow = mongoTemplate.findById(task.id, Workflow.class);

			Action action = null;
			while (action == null) {
				if (workflow.currentActionIds.size() == 0) {
					Node node = mongoTemplate.findById(workflow.startNodeId, Node.class);
					// todo: let node is tracable from action.

				}

				if (action.event == null) {
					// do it immediately
				} else {
					if (action.event.getStartTime() == null || action.event.getStartTime().isBeforeNow()) {
						// do it immediately
					} else {
						// add another task to the dao
					}
				}
			}

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

			mongoTemplate.insert(workflow);
		} catch (Exception e) {
			logger.error("[" + name + "] " + e.getMessage());
			logger.error(StringTool.exceptionToString(e));
		}
	}
}
