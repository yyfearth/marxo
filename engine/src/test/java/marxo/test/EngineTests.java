package marxo.test;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.batch.BatchRequest;
import com.restfb.batch.BatchResponse;
import com.restfb.types.Post;
import marxo.engine.EngineWorker;
import marxo.entity.FacebookData;
import marxo.entity.Task;
import marxo.entity.action.Action;
import marxo.entity.action.GenerateReportAction;
import marxo.entity.action.PostFacebookAction;
import marxo.entity.content.Content;
import marxo.entity.content.FacebookContent;
import marxo.entity.link.Link;
import marxo.entity.node.Event;
import marxo.entity.node.Node;
import marxo.entity.user.Tenant;
import marxo.entity.workflow.RunStatus;
import marxo.entity.workflow.Workflow;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.joda.time.Period;
import org.joda.time.Seconds;
import org.springframework.data.mongodb.core.query.Query;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public class EngineTests extends BasicDataTests {
	Thread workderThread;
	EngineWorker engineWorker;
	List<String> postIdsToRemove = new ArrayList<>();
	Tenant reusedTenant;
	FacebookClient facebookClient;

	@BeforeClass
	@Override
	public void beforeClass() throws Exception {
		super.beforeClass();

		reusedTenant = new Tenant();
		reusedTenant.setName("Marxo");
		reusedTenant.description = "A tall, a good guy, and a cat.";
		reusedTenant.phoneNumber = "(408) 888-8888";
		reusedTenant.email = "marxo@gmail.com";

		FacebookData facebookData = new FacebookData();
		facebookData.accessToken = "CAADCM9YpGYwBANeLvBD7aswljKFqsBYZAAUZC9ohrKoPkR0OQ8yZA1kMZAIwBuLFsxPnnRaUsuIjB40Q9i8qn2BNlaITfkKsQYE4LFatfAY6okQgYe4b8fYcr400YdQP98Wp4SFZBG6MOMCtC3pJNsZCVB3bBpXZCyKvbj66SwBWjBW1ZAAZBYT2a";
		facebookData.expireTime = DateTime.parse("2014-01-14T08:22:54.541Z");
		reusedTenant.facebookData = facebookData;

		insertEntities(
				reusedTenant
		);

		facebookClient = new DefaultFacebookClient(reusedTenant.facebookData.accessToken);

		engineWorker = new EngineWorker();
		workderThread = new Thread(engineWorker);
		workderThread.run();
	}

	@AfterClass
	@Override
	public void afterClass() throws Exception {
		engineWorker.isStopped = true;

		super.afterClass();

		if (!postIdsToRemove.isEmpty()) {
			logger.info(String.format("Remove %d posts", postIdsToRemove.size()));

			List<BatchRequest> deleteRequests = new ArrayList<>(postIdsToRemove.size());
			for (String postId : postIdsToRemove) {
				deleteRequests.add(new BatchRequest.BatchRequestBuilder(postId).method("DELETE").build());
			}
			List<BatchResponse> batchResponses = facebookClient.executeBatch(deleteRequests.toArray(new BatchRequest[deleteRequests.size()]));
//			for (BatchResponse batchResponse : batchResponses) {
//				logger.info(String.format("Responses: %s", batchResponse.getBody()));
//			}
		}

		workderThread.join(Seconds.seconds(5).toStandardDuration().getMillis());
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
	public void workflowWithoutNode() throws Exception {
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
		Assert.assertEquals(workflow.status, RunStatus.ERROR);
	}

	@Test
	public void simpleWorkflow() throws Exception {
		Workflow workflow = new Workflow();
		workflow.setTenant(reusedTenant);
		workflow.setName("Test Workflow for Engine");
		workflow.isProject = true;
		workflow.status = RunStatus.STARTED;

		Node node = new Node();
		node.setName("Test Node for Engine");
		workflow.addNode(node);

		PostFacebookAction postFacebookAction = new PostFacebookAction();
		postFacebookAction.setName("Test Action for Engine");
		postFacebookAction.isTracked = false;
		node.addAction(postFacebookAction);

		FacebookContent facebookContent = new FacebookContent();
		facebookContent.message = String.format("Marxo Engine Automation [%s]\nThat's one small step for the engine, a giant leap for the project", facebookContent.id);
		facebookContent.actionId = postFacebookAction.id;
		postFacebookAction.setContent(facebookContent);

		workflow.setStartNode(node);

		Task task = new Task(workflow.id);

		workflow.wire();

		insertEntities(
				workflow,
				node,
				facebookContent,
				task
		);

		engineWorker.run();

		workflow = Workflow.get(workflow.id);
		Assert.assertEquals(workflow.status, RunStatus.FINISHED);
		Assert.assertEquals(Task.count(), 0);

		node = Node.get(node.id);
		Assert.assertEquals(node.status, RunStatus.FINISHED);

		facebookContent = (FacebookContent) node.getActions().get(0).getContent();
		Assert.assertNotNull(facebookContent.publishMessageResponse);
		postIdsToRemove.add(facebookContent.publishMessageResponse.getId());
		Assert.assertEquals(facebookContent.message, facebookContent.message);
		Assert.assertNull(facebookContent.errorMessage);

		Post post = facebookClient.fetchObject(postFacebookAction.postId, Post.class);
		Assert.assertNotNull(post);
	}

	@Test
	public void actionWithDelay() throws Exception {
		Workflow workflow = new Workflow();
		workflow.setTenant(reusedTenant);
		workflow.setName("Test Workflow for Engine");
		workflow.isProject = true;
		workflow.status = RunStatus.STARTED;

		Node node = new Node();
		node.setName("Test Node for Engine");
		workflow.addNode(node);

		DummyAction dummyAction = new DummyAction();
		node.addAction(dummyAction);

		Event event = new Event();
		event.setName("Test event");
		event.getDuration();
		Task task = new Task(workflow.id);

		insertEntities(
				workflow,
				node,
				event,
				task
		);

		engineWorker.run();


	}

	@Test
	public void moreComplicatedWorkflow() throws Exception {
		Workflow workflow = new Workflow();
		workflow.setTenant(reusedTenant);
		workflow.isProject = true;
		workflow.status = RunStatus.STARTED;

		Node node = new Node();
		workflow.addNode(node);

		PostFacebookAction postFacebookAction = new PostFacebookAction();
		postFacebookAction.setName("Test Action for Engine");
		postFacebookAction.isTracked = false;
		node.addAction(postFacebookAction);

		FacebookContent facebookContent = new FacebookContent();
		facebookContent.message = String.format("Marxo Engine Automation [%s]\nThat's one small step for the engine, a giant leap for the project", facebookContent.id);
		facebookContent.actionId = postFacebookAction.id;
		postFacebookAction.setContent(facebookContent);

		Task task = new Task(workflow.id);

		workflow.wire();

		insertEntities(
				workflow,
				node,
				facebookContent,
				task
		);

		engineWorker.run();

		workflow = Workflow.get(workflow.id);
		Assert.assertEquals(workflow.status, RunStatus.FINISHED);
		Assert.assertEquals(Task.count(), 0);

		node = Node.get(node.id);
		Assert.assertEquals(node.status, RunStatus.FINISHED);

		facebookContent = (FacebookContent) node.getActions().get(0).getContent();
		Assert.assertNotNull(facebookContent.publishMessageResponse);
		postIdsToRemove.add(facebookContent.publishMessageResponse.getId());
		Assert.assertEquals(facebookContent.message, facebookContent.message);
		Assert.assertNull(facebookContent.errorMessage);

		Post post = facebookClient.fetchObject(postFacebookAction.postId, Post.class);
		Assert.assertNotNull(post);
	}

	@Test
	public void monitorPost() throws Exception {
		Workflow workflow = new Workflow();
		workflow.setTenant(reusedTenant);
		workflow.createTime = workflow.updateTime = DateTime.now();
		workflow.setName("Test workflow");

		int nodeCount = 1;
		int contentCount = 1;

		Node node1 = new Node();
		node1.setName("Test Node " + nodeCount++);
		workflow.addNode(node1);

		PostFacebookAction postFacebookAction = new PostFacebookAction();
		postFacebookAction.setName("Test Post to Facebook 1");
		postFacebookAction.monitorPeriod = Period.seconds(5);
		node1.addAction(postFacebookAction);

		Event event = new Event();
		event.setName("Test Trace for 5 mins");
		event.setDuration(Minutes.minutes(5).toStandardDuration());
		postFacebookAction.setEvent(event);

		FacebookContent facebookContent1 = new FacebookContent();
		facebookContent1.setName("Test Contnet " + contentCount);
		facebookContent1.message = String.format("Marxo Engine is monitoring this message\n\n%s", facebookContent1);
		postFacebookAction.setContent(facebookContent1);

		Node node2 = new Node();
		node2.setName("Test Node " + nodeCount);
		workflow.addNode(node2);

		GenerateReportAction generateReportAction = new GenerateReportAction();
		generateReportAction.addMonitoredAction(postFacebookAction);
		node2.addAction(generateReportAction);

		Link link = new Link();
		link.setName("Test link");
		link.setPreviousNode(node1);
		link.setNextNode(node2);
		workflow.addLink(link);

		workflow.wire();

		Task task = new Task(workflow.id);

		insertEntities(
				workflow,
				node1,
				postFacebookAction,
				facebookContent1,
				event,
				node2,
				generateReportAction,
				link,
				task
		);

		engineWorker.run();

		facebookContent1 = (FacebookContent) Content.get(facebookContent1.id);

		Assert.assertEquals(Task.count(), 0);

		workflow = Workflow.get(workflow.id);
		Assert.assertEquals(workflow.status, RunStatus.MONITORING);
		Assert.assertEquals(workflow.tracableActionIds.size(), 1);

		node1 = Node.get(node1.id);
		Assert.assertEquals(node1.status, RunStatus.FINISHED);

		postFacebookAction = (PostFacebookAction) Action.get(postFacebookAction.id);
		postIdsToRemove.add(postFacebookAction.postId);
		Assert.assertEquals(postFacebookAction.status, RunStatus.MONITORING);

		link = Link.get(link.id);
		Assert.assertEquals(link.status, RunStatus.FINISHED);

		node2 = Node.get(node2.id);
		Assert.assertEquals(node2.status, RunStatus.FINISHED);

		generateReportAction = (GenerateReportAction) Action.get(generateReportAction.id);
		Assert.assertNotNull(generateReportAction.getReport());
	}

	@Test
	public void pauseProject() throws Exception {
		Assert.fail();
	}

	@Test
	public void resumeProject() throws Exception {
		Assert.fail();
	}
}
