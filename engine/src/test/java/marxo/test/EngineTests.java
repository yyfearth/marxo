package marxo.test;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.batch.BatchRequest;
import com.restfb.batch.BatchResponse;
import com.restfb.types.Post;
import marxo.engine.EngineWorker;
import marxo.entity.FacebookData;
import marxo.entity.Task;
import marxo.entity.action.MonitorFacebookAction;
import marxo.entity.action.PostFacebookAction;
import marxo.entity.content.FacebookContent;
import marxo.entity.content.FacebookMonitorContent;
import marxo.entity.node.Event;
import marxo.entity.node.Node;
import marxo.entity.user.Tenant;
import marxo.entity.workflow.RunStatus;
import marxo.entity.workflow.Workflow;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
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
	EngineWorker engineWorker = new EngineWorker();
	List<String> postIdsToRemove = new ArrayList<>();
	FacebookData facebookData;
	Tenant tenant;
	FacebookClient facebookClient;

	@BeforeClass
	@Override
	public void beforeClass() throws Exception {
		super.beforeClass();

		tenant = new Tenant();
		tenant.setName("Marxo");
		tenant.description = "A tall, a good guy, and a cat.";
		tenant.phoneNumber = "(408) 888-8888";
		tenant.email = "marxo@gmail.com";

		facebookData = new FacebookData();
		facebookData.accessToken = "CAADCM9YpGYwBANeLvBD7aswljKFqsBYZAAUZC9ohrKoPkR0OQ8yZA1kMZAIwBuLFsxPnnRaUsuIjB40Q9i8qn2BNlaITfkKsQYE4LFatfAY6okQgYe4b8fYcr400YdQP98Wp4SFZBG6MOMCtC3pJNsZCVB3bBpXZCyKvbj66SwBWjBW1ZAAZBYT2a";
		facebookData.expireTime = DateTime.parse("2014-01-14T08:22:54.541Z");
		tenant.facebookData = facebookData;

		tenant.save();
		entitiesToRemove.add(tenant);

		facebookClient = new DefaultFacebookClient(tenant.facebookData.accessToken);
	}

	@AfterClass
	@Override
	public void afterClass() throws Exception {
		super.afterClass();

		if (!postIdsToRemove.isEmpty()) {
			List<BatchRequest> deleteRequests = new ArrayList<>(postIdsToRemove.size());
			for (String postId : postIdsToRemove) {
				deleteRequests.add(new BatchRequest.BatchRequestBuilder(postId).method("DELETE").build());
			}
			List<BatchResponse> batchResponses = facebookClient.executeBatch(deleteRequests.toArray(new BatchRequest[deleteRequests.size()]));
			for (BatchResponse batchResponse : batchResponses) {
				logger.info(String.format("Responses: %s", batchResponse.getBody()));
			}
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
	public void oneNodeAndOneAction() throws Exception {
		Workflow workflow = new Workflow();
		workflow.setTenant(tenant);
		workflow.setName("Test Workflow for Engine");
		workflow.isProject = true;
		workflow.status = RunStatus.STARTED;

		Node node = new Node();
		node.setName("Test Node for Engine");
		workflow.addNode(node);

		PostFacebookAction postFacebookAction = new PostFacebookAction();
		postFacebookAction.setName("Test Action for Engine");
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

		Post post = facebookClient.fetchObject(facebookContent.postId, Post.class);
		Assert.assertNotNull(post);
	}

	@Test
	public void trackMessage() throws Exception {
		Workflow workflow = new Workflow();
		workflow.setTenant(tenant);
		workflow.isProject = true;
		workflow.status = RunStatus.STARTED;

		Node node = new Node();
		workflow.addNode(node);

		PostFacebookAction postFacebookAction = new PostFacebookAction();
		node.addAction(postFacebookAction);

		FacebookContent facebookContent = new FacebookContent();
		facebookContent.message = String.format("Marxo Engine Automation [%s]\nThat's one small step for the engine, a giant leap for the project", facebookContent.id);
		facebookContent.actionId = postFacebookAction.id;
		postFacebookAction.setContent(facebookContent);

		// Monitor action
		MonitorFacebookAction monitorFacebookAction = new MonitorFacebookAction();
		monitorFacebookAction.period = Period.seconds(5);
		monitorFacebookAction.monitoredActionKey = String.format("%s.%s.%s", workflow.key, node.key, postFacebookAction.key);

		FacebookMonitorContent facebookMonitorContent = new FacebookMonitorContent();
		monitorFacebookAction.setContent(facebookMonitorContent);

		Event event = new Event();
		event.setDuration(Seconds.seconds(10).toStandardDuration());
		monitorFacebookAction.setEvent(event);

		node.addAction(monitorFacebookAction);

		Task task = new Task(workflow.id);

		// Save all entities
		workflow.wire();
		insertEntities(
				workflow,
				node,
				facebookContent,
				facebookMonitorContent,
				event,
				task
		);

		engineWorker.run();

		// Verification
		node = Node.get(node.id);
		Assert.assertEquals(node.status, RunStatus.FINISHED);

		facebookContent = (FacebookContent) node.getActions().get(0).getContent();
		Assert.assertNotNull(facebookContent.publishMessageResponse);
		postIdsToRemove.add(facebookContent.publishMessageResponse.getId());
		Assert.assertEquals(facebookContent.message, facebookContent.message);
		Assert.assertNull(facebookContent.errorMessage);

		Post post = facebookClient.fetchObject(facebookContent.postId, Post.class);
		Assert.assertNotNull(post);

//		Thread.sleep(event.getDuration().getMillis());  // Wait for the worker to finish the monitoring.

		monitorFacebookAction = (MonitorFacebookAction) node.getActions().get(1);
		Assert.assertEquals(monitorFacebookAction.status, RunStatus.FINISHED);

		facebookMonitorContent = monitorFacebookAction.getContent();
		Assert.assertEquals(facebookMonitorContent.records.size(), 2);
	}
}
