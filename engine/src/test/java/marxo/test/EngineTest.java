package marxo.test;

import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.types.Post;
import marxo.engine.EngineWorker;
import marxo.entity.BasicEntity;
import marxo.entity.FacebookData;
import marxo.entity.Task;
import marxo.entity.content.FacebookContent;
import marxo.entity.node.Node;
import marxo.entity.node.PostFacebook;
import marxo.entity.user.Tenant;
import marxo.entity.workflow.RunStatus;
import marxo.entity.workflow.Workflow;
import marxo.validation.SelectIdFunction;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
		Criteria criteria = Criteria.where("_id").in(Collections2.transform(entitiesToRemove, SelectIdFunction.getInstance()));
		for (String collectionName : mongoTemplate.getCollectionNames()) {
			mongoTemplate.remove(Query.query(criteria), collectionName);
		}
	}

	@Test
	public void testName() throws Exception {
		Criteria criteria = Criteria.where("id").in(Collections2.transform(entitiesToRemove, SelectIdFunction.getInstance()));
		for (String collectionName : mongoTemplate.getCollectionNames()) {
//			mongoTemplate.remove(Query.query(criteria), collectionName);
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
		List<BasicEntity> entitiesToSave = new ArrayList<>();

		Tenant tenant = new Tenant();
		tenant.setName("Marxo");
		tenant.description = "A tall, a good guy, and a cat.";
		tenant.phoneNumber = "(408) 888-8888";
		tenant.email = "marxo@gmail.com";

		FacebookData facebookData = new FacebookData();
		facebookData.accessToken = "CAADCM9YpGYwBANeLvBD7aswljKFqsBYZAAUZC9ohrKoPkR0OQ8yZA1kMZAIwBuLFsxPnnRaUsuIjB40Q9i8qn2BNlaITfkKsQYE4LFatfAY6okQgYe4b8fYcr400YdQP98Wp4SFZBG6MOMCtC3pJNsZCVB3bBpXZCyKvbj66SwBWjBW1ZAAZBYT2a";
		facebookData.expireTime = DateTime.parse("2014-01-14T08:22:54.541Z");
		tenant.facebookData = facebookData;

		Assert.assertTrue(mongoTemplate.exists(Query.query(Criteria.where("name").is("Marxo")), Tenant.class));

		Workflow workflow = new Workflow();
		workflow.setName("Test Workflow for Engine");

		Node node = new Node();
		node.setName("Test Node for Engine");
		workflow.nodeIds.add(node.id);

		PostFacebook postFacebook = new PostFacebook();
		postFacebook.setName("Test Action for Engine");
		node.getActions().add(postFacebook);

		FacebookContent content = new FacebookContent();
		postFacebook.contentId = content.id;
		content.message = "Marxo Engine Automation\nThat's one small step for the engine, a giant leap for the project";
		content.actionId = postFacebook.id;

		workflow.startNodeId = node.id;
		workflow.status = RunStatus.STARTED;

		Task task = new Task(workflow.id);

		entitiesToSave.addAll(Lists.newArrayList(
				tenant,
				workflow,
				node,
				content,
				task
		));
		mongoTemplate.insertAll(entitiesToSave);

		entitiesToRemove.addAll(entitiesToSave);

		engineWorker.run();

		workflow = Workflow.get(workflow.id);
		Assert.assertEquals(workflow.status, RunStatus.FINISHED);
		Assert.assertEquals(Task.count(), 0);

		node = Node.get(node.id);
		Assert.assertEquals(node.status, RunStatus.FINISHED);

		FacebookContent content1 = (FacebookContent) node.getActions().get(0).getContent();
		Assert.assertNotNull(content.postId);

		FacebookClient facebookClient = new DefaultFacebookClient(tenant.facebookData.accessToken);
		Post post = facebookClient.fetchObject(content1.postId, Post.class);
		Assert.assertEquals(content1.message, content.message);

		tenant.remove();
		workflow.remove();
		node.remove();
		content.remove();
		task.remove();
	}
}
