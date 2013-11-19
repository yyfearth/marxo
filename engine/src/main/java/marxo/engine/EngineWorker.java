package marxo.engine;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import marxo.dao.*;
import marxo.entity.Task;
import marxo.entity.node.Action;
import marxo.entity.node.Event;
import marxo.entity.node.Node;
import marxo.entity.workflow.Workflow;
import marxo.tool.Loggable;
import marxo.tool.StringTool;
import org.springframework.beans.factory.annotation.Autowired;

public class EngineWorker implements Runnable, Loggable {
	@Autowired
	WorkflowDao workflowDao;
	@Autowired
	NodeDao nodeDao;
	@Autowired
	EventDao eventDao;
	String name;

	public EngineWorker(String name) {
		this.name = name;
	}

	// todo: make the method thread-safe, ready for multi-thread.
	@Override
	public void run() {
		try {
			TaskDao taskDao = new TaskDao();
			Task task = taskDao.findAndRemove();

			if (task == null) {
				return;
			}

			Workflow workflow = workflowDao.findOne(task.id);

			Action action = null;
			while (action == null) {
				if (workflow.currentActionIds.size() == 0) {
					Node node = nodeDao.findOne(workflow.startNodeId);
				}

				if (action.eventId == null) {
					// do it immediately
				} else {
					Event event = eventDao.findOne(action.eventId);
					if (event.getStartTime() == null) {
						// do it immediately
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

			workflowDao.insert(workflow);
		} catch (Exception e) {
			logger.error("[" + name + "] " + e.getMessage());
			logger.error(StringTool.exceptionToString(e));
		}
	}
}
