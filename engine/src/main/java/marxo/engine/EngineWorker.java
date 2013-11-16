package marxo.engine;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import marxo.dao.DataPair;
import marxo.dao.NodeDao;
import marxo.dao.TaskDao;
import marxo.dao.WorkflowDao;
import marxo.entity.Node;
import marxo.entity.Task;
import marxo.entity.Workflow;
import marxo.entity.action.Action;
import marxo.tool.Loggable;
import marxo.tool.StringTool;

public class EngineWorker implements Runnable, Loggable {
	WorkflowDao workflowDao;
	NodeDao nodeDao;
	String name;

	public EngineWorker(String name) {
		this.name = name;
	}

	// todo: make the method thread-safe, ready for multi-thread.
	@Override
	public void run() {
		try {
			workflowDao = new WorkflowDao(null, true);
			nodeDao = new NodeDao(null);

			TaskDao taskDao = new TaskDao();
			Task task = taskDao.findAndRemove(Lists.<DataPair>newArrayList());

			if (task == null) {
				return;
			}

			Workflow workflow = workflowDao.findOne(task.id);

			if (workflow.startNodeId == null) {
				workflow.currentNodeId = workflow.startNodeId;
			}

			Node node = nodeDao.findOne(workflow.currentNodeId);

			if (workflow.currentActionId == null) {
				if (node.actions.size() == 0) {
					// todo: end this node.
				}
				workflow.currentActionId = node.actions.get(0).id;
			}

			final Workflow w = workflow;

			Action action = Iterables.find(node.actions, new Predicate<Action>() {
				@Override
				public boolean apply(Action input) {
					return input.id.equals(w.currentActionId);
				}
			});

			// todo: do if the schedule hits

			workflowDao.insert(workflow);
		} catch (Exception e) {
			logger.error("[" + name + "] " + e.getMessage());
			logger.error(StringTool.exceptionToString(e));
		}
	}
}
