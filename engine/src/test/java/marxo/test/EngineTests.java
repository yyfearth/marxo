package marxo.test;

import com.google.common.collect.Lists;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.batch.BatchRequest;
import com.restfb.batch.BatchResponse;
import com.restfb.types.Post;
import marxo.engine.EngineWorker;
import marxo.entity.FacebookData;
import marxo.entity.Task;
import marxo.entity.content.FacebookContent;
import marxo.entity.node.Node;
import marxo.entity.node.PostFacebook;
import marxo.entity.user.Tenant;
import marxo.entity.workflow.RunStatus;
import marxo.entity.workflow.Workflow;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
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

		BatchRequest.BatchRequestBuilder batchRequestBuilder = new BatchRequest.BatchRequestBuilder("me/feed");
		List<BatchRequest> deleteRequests = new ArrayList<>(postIdsToRemove.size());
		for (String postId : postIdsToRemove) {
			deleteRequests.add(new BatchRequest.BatchRequestBuilder(postId).method("DELETE").build());
		}
		List<BatchResponse> batchResponses = facebookClient.executeBatch(deleteRequests.toArray(new BatchRequest[deleteRequests.size()]));
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

		PostFacebook postFacebook = new PostFacebook();
		postFacebook.setName("Test Action for Engine");
		node.addAction(postFacebook);

		FacebookContent content = new FacebookContent();
		content.message = String.format("Marxo Engine Automation [%s]\nThat's one small step for the engine, a giant leap for the project", content.id);
		content.actionId = postFacebook.id;
		postFacebook.setContent(content);

		workflow.setStartNode(node);

		Task task = new Task(workflow.id);

		workflow.wire();

		entitiesToInsert.addAll(Lists.newArrayList(
				tenant,
				workflow,
				node,
				content,
				task
		));
		insertEntities();

		engineWorker.run();

		workflow = Workflow.get(workflow.id);
		Assert.assertEquals(workflow.status, RunStatus.FINISHED);
		Assert.assertEquals(Task.count(), 0);

		node = Node.get(node.id);
		Assert.assertEquals(node.status, RunStatus.FINISHED);

		FacebookContent facebookContent = (FacebookContent) node.getActions().get(0).getContent();
		Assert.assertNotNull(facebookContent.postId);
		Assert.assertEquals(facebookContent.message, content.message);

		Post post = facebookClient.fetchObject(facebookContent.postId, Post.class);
		Assert.assertNotNull(post);

		postIdsToRemove.add(facebookContent.postId);
	}
}
