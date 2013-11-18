package marxo.engine;

import com.google.common.collect.Lists;
import marxo.dao.*;
import marxo.entity.Task;
import marxo.entity.content.FacebookContent;
import marxo.entity.node.Node;
import marxo.entity.node.PostFacebook;
import marxo.entity.workflow.ProjectStatus;
import marxo.entity.workflow.Workflow;
import marxo.validation.SelectIdFunction;
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
	EventDao eventDao = new EventDao();
	ContentDao contentDao = new ContentDao();
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
		workflow.setName("Test Workflow for Engine");
		workflow.fillWithDefaultValues();
		workflowsToDelete.add(workflow);

		Node node = new Node();
		node.setName("Test Node for Engine");
		node.fillWithDefaultValues();
		workflow.nodeIds.add(node.id);

		PostFacebook postFacebook = new PostFacebook();
		postFacebook.setName("Test Action for Engine");
		postFacebook.fillWithDefaultValues();
		node.actions.add(postFacebook);

		FacebookContent facebookContent = new FacebookContent();
		facebookContent.fillWithDefaultValues();
		postFacebook.contentId = facebookContent.id;
		facebookContent.message = "Marxo Engine Automation\nThat's one small step for the engine, a giant leap for the project";
		facebookContent.actionId = postFacebook.id;
		contentDao.insert(facebookContent);

		workflow.startNodeId = node.id;
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
}
