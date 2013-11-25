package marxo.engine;

import com.google.common.collect.Collections2;
import marxo.entity.BasicEntity;
import marxo.entity.Task;
import marxo.entity.content.FacebookContent;
import marxo.entity.node.Node;
import marxo.entity.node.PostFacebook;
import marxo.entity.workflow.RunStatus;
import marxo.entity.workflow.Workflow;
import marxo.validation.SelectIdFunction;
import org.bson.types.ObjectId;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.*;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unchecked")
public class EngineTest {
	protected static final ApplicationContext applicationContext = new ClassPathXmlApplicationContext("mongo-configuration.xml");
	protected static final MongoTemplate mongoTemplate = applicationContext.getBean(MongoTemplate.class);
	EngineWorker engineWorker = new EngineWorker();
	Set<BasicEntity> entitiesToRemove = new HashSet<>();

	@BeforeMethod
	public void beforeMethod() throws Exception {

	}

	@AfterMethod
	public void afterMethod() throws Exception {

	}

	@BeforeClass
	public void beforeClass() throws Exception {
	}

	@AfterClass
	public void afterClass() throws Exception {
		Criteria criteria = Criteria.where("id").in(Collections2.transform(entitiesToRemove, SelectIdFunction.getInstance()));
		for (String collectionName : mongoTemplate.getCollectionNames()) {
			mongoTemplate.remove(Query.query(criteria), collectionName);
		}
	}

	@Test
	public void noTask() throws Exception {
		long taskCount = mongoTemplate.count(new Query(), Task.class);
		if (taskCount != 0) {
			throw new SkipException("Skip since tasks collection is " + taskCount);
		}

		engineWorker.run();

		Assert.assertEquals(mongoTemplate.count(new Query(), Task.class), 0);
	}

	@Test
	public void notExistWorkflow() throws Exception {
		Task task = new Task(new ObjectId());
		task.save();
		entitiesToRemove.add(task);

		engineWorker.run();

		Assert.assertNull(Task.get(task.id));
	}

	@Test
	public void workflowWithNoNode() throws Exception {
		Workflow workflow = new Workflow();
		workflow.setName(getClass().toString());
		workflow.isProject = true;
		workflow.status = RunStatus.STARTED;
		workflow.save();
		entitiesToRemove.add(workflow);

		Task task = new Task(workflow.id);
		task.save();
		entitiesToRemove.add(task);

		engineWorker.run();

		workflow = Workflow.get(workflow.id);
		Assert.assertEquals(workflow.status, RunStatus.FINISHED);
	}

	@Test
	public void oneNodeAndOneAction() throws Exception {
		Workflow workflow = new Workflow();
		workflow.setName("Test Workflow for Engine");
		entitiesToRemove.add(workflow);

		Node node = new Node();
		node.setName("Test Node for Engine");
		workflow.nodeIds.add(node.id);

		PostFacebook postFacebook = new PostFacebook();
		postFacebook.setName("Test Action for Engine");
		node.actions.add(postFacebook);
		node.save();

		FacebookContent facebookContent = new FacebookContent();
		postFacebook.contentId = facebookContent.id;
		facebookContent.message = "Marxo Engine Automation\nThat's one small step for the engine, a giant leap for the project";
		facebookContent.actionId = postFacebook.id;
		facebookContent.save();

		workflow.startNodeId = node.id;
		workflow.status = RunStatus.STARTED;
		workflow.save();

		Task task = new Task(workflow.id);
		task.save();
		entitiesToRemove.add(task);

		engineWorker.run();

		node = Node.get(node.id);
		Assert.assertEquals(node.status, RunStatus.FINISHED);

		workflow = Workflow.get(workflow.id);
		Assert.assertEquals(workflow.status, RunStatus.FINISHED);
		Assert.assertEquals(mongoTemplate.count(new Query(), Task.class), 0);
	}
}
