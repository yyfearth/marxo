package marxo.engine;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import marxo.dao.DataPair;
import marxo.dao.DataPairOperator;
import marxo.dao.TaskDao;
import marxo.dao.WorkflowDao;
import marxo.entity.Node;
import marxo.entity.ProjectStatus;
import marxo.entity.Task;
import marxo.entity.Workflow;
import marxo.entity.action.Action;
import org.bson.types.ObjectId;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class EngineTest {
	final SelectIdFunction selectIdFunction = new SelectIdFunction();
	WorkflowDao workflowDao = new WorkflowDao(null, true);
	TaskDao taskDao = new TaskDao();
	List<Workflow> workflowsToDelete = new ArrayList<>();

	@BeforeMethod
	public void setUp() throws Exception {

	}

	@AfterMethod
	public void tearDown() throws Exception {
	}

	@AfterClass
	public void afterClass() throws Exception {
		workflowDao.delete(Lists.newArrayList(
				new DataPair("id", DataPairOperator.IN, Lists.transform(workflowsToDelete, selectIdFunction))
		));
		taskDao.remove(Lists.<DataPair>newArrayList());
	}

	@Test
	public void testWorkerDirectly() throws Exception {
		Workflow workflow = new Workflow();
		workflow.name = "Test Workflow for Engine";
		workflow.fillWithDefaultValues();
		workflowsToDelete.add(workflow);

		Node node = new Node();
		node.name = "Test Node for Engine";
		node.fillWithDefaultValues();
		workflow.nodeIds.add(node.id);

		Action action = new Action();
		action.name = "Test Action for Engine";
		action.fillWithDefaultValues();
		node.actions.add(action);

		workflow.startNodeId = node.id;
		workflow.endNodeId = node.id;
		workflow.status = ProjectStatus.STARTED;

		workflowDao.save(workflow);

		Task task = new Task(workflow.id);
		taskDao.insert(task);

		EngineWorker engineWorker = new EngineWorker("Worker");
		engineWorker.run();

		workflow = workflowDao.findOne(workflow.id);
		Assert.assertEquals(workflow.status, ProjectStatus.FINISHED);
		Assert.assertEquals(taskDao.count(), 0);
	}

	class SelectIdFunction implements Function<Workflow, ObjectId> {
		@Override
		public ObjectId apply(Workflow input) {
			return input.id;
		}
	}
}
