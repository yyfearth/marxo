package marxo.engine;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import marxo.dao.NodeDao;
import marxo.dao.WorkflowDao;
import marxo.entity.Action;
import marxo.entity.Node;
import marxo.entity.Workflow;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Map;

public class EngineWorker implements Runnable {
	@Autowired
	WorkflowDao workflowDao;
	@Autowired
	NodeDao nodeDao;

	// todo: make the method thread-safe, ready for multi-thread.
	@Override
	public void run() {
		Workflow workflow = workflowDao.getNextProject();

		if (workflow.processingNodeId == null) {
//			workflow
		} else {
			Node node = nodeDao.get(workflow.processingNodeId);

			if (node == null) {
				throw new NotImplementedException();
			}

			Map<ObjectId, Action> actionMap = Maps.uniqueIndex(node.actions, new Function<Action, ObjectId>() {
				@Override
				public ObjectId apply(Action input) {
					return input.id;
				}
			});

			if (!actionMap.containsKey(workflow.processingActionId)) {

			}
		}
	}
}
