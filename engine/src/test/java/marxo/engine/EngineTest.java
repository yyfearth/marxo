package marxo.engine;

import com.google.common.collect.Lists;
import marxo.entity.Task;
import marxo.entity.content.FacebookContent;
import marxo.entity.node.Node;
import marxo.entity.node.PostFacebook;
import marxo.entity.workflow.ProjectStatus;
import marxo.entity.workflow.Workflow;
import marxo.validation.SelectIdFunction;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;

public class EngineTest {
	protected static final ApplicationContext applicationContext = new ClassPathXmlApplicationContext("mongo-configuration.xml");
	protected static final MongoTemplate mongoTemplate = applicationContext.getBean(MongoTemplate.class);
	ArrayList<Workflow> workflowsToDelete = new ArrayList<>();

	@BeforeMethod
	public void setUp() throws Exception {

	}

	@AfterMethod
	public void tearDown() throws Exception {
	}

	@AfterClass
	public void afterClass() throws Exception {
		Criteria criteria = Criteria.where("id").in(Lists.transform(workflowsToDelete, SelectIdFunction.getInstance()));
		mongoTemplate.remove(Query.query(criteria), Workflow.class);
		mongoTemplate.remove(new Query(), Task.class);
	}

	@Test
	public void testWorkerDirectly() throws Exception {
		Workflow workflow = new Workflow();
		workflow.setName("Test Workflow for Engine");
		workflowsToDelete.add(workflow);

		Node node = new Node();
		node.setName("Test Node for Engine");
		workflow.nodeIds.add(node.id);

		PostFacebook postFacebook = new PostFacebook();
		postFacebook.setName("Test Action for Engine");
		node.actions.add(postFacebook);

		FacebookContent facebookContent = new FacebookContent();
		postFacebook.contentId = facebookContent.id;
		facebookContent.message = "Marxo Engine Automation\nThat's one small step for the engine, a giant leap for the project";
		facebookContent.actionId = postFacebook.id;

		mongoTemplate.insert(facebookContent);

		workflow.startNodeId = node.id;
		workflow.status = ProjectStatus.STARTED;

		mongoTemplate.save(workflow);

		Task task = new Task(workflow.id);
		mongoTemplate.insert(task);

		EngineWorker engineWorker = new EngineWorker("Worker");
		engineWorker.run();

		workflow = mongoTemplate.findById(workflow.id, Workflow.class);
		Assert.assertEquals(workflow.status, ProjectStatus.FINISHED);
		Assert.assertEquals(mongoTemplate.count(new Query(), Task.class), 0);
	}
}
