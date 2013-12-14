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
import marxo.entity.action.Content;
import marxo.entity.action.FacebookAction;
import marxo.entity.action.WaitAction;
import marxo.entity.link.Link;
import marxo.entity.node.Event;
import marxo.entity.node.Node;
import marxo.entity.user.Tenant;
import marxo.entity.workflow.RunStatus;
import marxo.entity.workflow.Workflow;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Minutes;
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

		EngineWorker.startAsync();
	}

	@AfterClass
	@Override
	public void afterClass() throws Exception {
		EngineWorker.stopAsync();

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
	}

	@Test
	public void noTask() throws Exception {
		long taskCount = mongoTemplate.count(new Query(), Task.class);
		if (taskCount != 0) {
			throw new SkipException("Skip since tasks collection is " + taskCount);
		}

		Assert.assertEquals(mongoTemplate.count(new Query(), Task.class), 0);
	}

	@Test
	public void notExistWorkflow() throws Exception {
		Task task = new Task(new ObjectId());
		task.save();
		entitiesToRemove.add(task);

		Assert.assertNull(Task.get(task.id));
	}

	@Test
	public void workflowWithoutNode() throws Exception {
		Workflow workflow = new Workflow();
		workflow.setName(getClass().toString());
		workflow.isProject = true;
		workflow.setStatus(RunStatus.STARTED);
		workflow.save();
		entitiesToRemove.add(workflow);

		Task task = new Task(workflow.id);
		task.save();
		entitiesToRemove.add(task);

		workflow = Workflow.get(workflow.id);
		Assert.assertEquals(workflow.getStatus(), RunStatus.ERROR);
	}

	@Test
	public void simpleWorkflow() throws Exception {
		Workflow workflow = new Workflow();
		workflow.setTenant(reusedTenant);
		workflow.setName("Test Workflow for Engine");
		workflow.isProject = true;
		workflow.setStatus(RunStatus.STARTED);

		Node node = new Node();
		node.setName("Test Node for Engine");
		workflow.addNode(node);

		FacebookAction postFacebookAction = new FacebookAction();
		postFacebookAction.setName("Test Action for Engine");
		node.addAction(postFacebookAction);

		Content facebookContent = new Content(Content.Type.FACEBOOK);
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

		workflow = Workflow.get(workflow.id);
		Assert.assertEquals(workflow.getStatus(), RunStatus.FINISHED);
		Assert.assertEquals(Task.count(), 0);

		node = Node.get(node.id);
		Assert.assertEquals(node.getStatus(), RunStatus.FINISHED);

		facebookContent = (Content) node.getActions().get(0).getContent();
		Assert.assertNotNull(facebookContent.messageResponse);
		postIdsToRemove.add(facebookContent.getPostId());
		Assert.assertEquals(facebookContent.message, facebookContent.message);

		Post post = facebookClient.fetchObject(postFacebookAction.getContent().getPostId(), Post.class);
		Assert.assertNotNull(post);
	}

	@Test
	public void actionWithDelay() throws Exception {
		Workflow workflow = new Workflow();
		workflow.setTenant(reusedTenant);
		workflow.setName("Test Workflow for Engine");
		workflow.isProject = true;
		workflow.setStatus(RunStatus.STARTED);

		Node node = new Node();
		node.setName("Test Node for Engine");
		workflow.addNode(node);

		Action action = new WaitAction();
		node.addAction(action);

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
	}

	@Test
	public void moreComplicatedWorkflow() throws Exception {
		Workflow workflow = new Workflow();
		workflow.setTenant(reusedTenant);
		workflow.isProject = true;
		workflow.setStatus(RunStatus.STARTED);

		Node node = new Node();
		workflow.addNode(node);

		FacebookAction action = new FacebookAction();
		action.setName("Test Action for Engine");
		node.addAction(action);

		Content facebookContent = new Content(Content.Type.FACEBOOK);
		facebookContent.message = String.format("Marxo Engine Automation [%s]\nThat's one small step for the engine, a giant leap for the project", facebookContent.id);
		facebookContent.actionId = action.id;
		action.setContent(facebookContent);

		Task task = new Task(workflow.id);

		workflow.wire();

		insertEntities(
				workflow,
				node,
				facebookContent,
				task
		);

		workflow = Workflow.get(workflow.id);
		Assert.assertEquals(workflow.getStatus(), RunStatus.FINISHED);
		Assert.assertEquals(Task.count(), 0);

		node = Node.get(node.id);
		Assert.assertEquals(node.getStatus(), RunStatus.FINISHED);

		facebookContent = (Content) node.getActions().get(0).getContent();
		Assert.assertNotNull(facebookContent.messageResponse);
		postIdsToRemove.add(facebookContent.getPostId());
		Assert.assertEquals(facebookContent.message, facebookContent.message);

		Post post = facebookClient.fetchObject(action.getContent().getPostId(), Post.class);
		Assert.assertNotNull(post);
	}

	@Test
	public void trackingPost() throws Exception {
		Workflow workflow = new Workflow();
		workflow.setTenant(reusedTenant);
		workflow.createTime = workflow.updateTime = DateTime.now();
		workflow.setName("Test workflow");

		int nodeCount = 1;
		int contentCount = 1;

		Node node1 = new Node();
		node1.setName("Test Node " + nodeCount++);
		workflow.addNode(node1);

		FacebookAction action = new FacebookAction();
		action.setName("Test Post to Facebook 1");
		action.trackEvent = new Event(DateTime.now(), Days.ONE.toStandardDuration());
		node1.addAction(action);

		Event event = new Event();
		event.setName("Test Trace for 5 mins");
		event.setDuration(Minutes.minutes(5).toStandardDuration());
		action.setEvent(event);

		Content facebookContent1 = new Content(Content.Type.FACEBOOK);
		facebookContent1.setName("Test Contnet " + contentCount);
		facebookContent1.message = String.format("Marxo Engine is tracking this message\n\n%s", facebookContent1);
		action.setContent(facebookContent1);

		Node node2 = new Node();
		node2.setName("Test Node " + nodeCount);
		workflow.addNode(node2);

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
				action,
				facebookContent1,
				event,
				node2,
				link,
				task
		);

		facebookContent1 = (Content) Content.get(facebookContent1.id);

		Assert.assertEquals(Task.count(), 0);

		workflow = Workflow.get(workflow.id);
		Assert.assertEquals(workflow.getStatus(), RunStatus.TRACKED);
		Assert.assertEquals(workflow.trackedActionIds.size(), 1);

		node1 = Node.get(node1.id);
		Assert.assertEquals(node1.getStatus(), RunStatus.FINISHED);

		action = (FacebookAction) Action.get(action.id);
		postIdsToRemove.add(action.getContent().getPostId());
		Assert.assertEquals(action.getStatus(), RunStatus.TRACKED);

		link = Link.get(link.id);
		Assert.assertEquals(link.getStatus(), RunStatus.FINISHED);

		node2 = Node.get(node2.id);
	}

	@Test
	public void pauseProject() throws Exception {
		Assert.fail();
	}

	@Test
	public void resumeProject() throws Exception {
		Assert.fail();
	}

	@Test
	public void withInvalidAccessToken() throws Exception {
		Assert.fail();
	}
}
