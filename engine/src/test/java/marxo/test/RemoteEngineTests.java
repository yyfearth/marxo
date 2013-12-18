package marxo.test;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.batch.BatchRequest;
import com.restfb.batch.BatchResponse;
import marxo.entity.BasicEntity;
import marxo.entity.Task;
import marxo.entity.action.Action;
import marxo.entity.action.Content;
import marxo.entity.action.FacebookAction;
import marxo.entity.link.Link;
import marxo.entity.node.Event;
import marxo.entity.node.Node;
import marxo.entity.user.Tenant;
import marxo.entity.workflow.RunStatus;
import marxo.entity.workflow.Workflow;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.joda.time.Seconds;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public class RemoteEngineTests extends BasicDataTests {
	List<String> postIdsToRemove = new ArrayList<>();
	Tenant reusedTenant;
	FacebookClient facebookClient;

	@BeforeClass
	@Override
	public void beforeClass() throws Exception {
		super.beforeClass();

		reusedTenant = mongoTemplate.findOne(Query.query(Criteria.where("name").is("Marxo")), Tenant.class);
		facebookClient = new DefaultFacebookClient(reusedTenant.facebookData.accessToken);

		Query queryForTest = Query.query(Criteria.where("name").regex("[tT]est"));
		mongoTemplate.remove(queryForTest, Workflow.class);
		mongoTemplate.remove(queryForTest, Node.class);
		mongoTemplate.remove(queryForTest, Action.class);
		mongoTemplate.remove(queryForTest, Content.class);
		mongoTemplate.remove(new Query(), Task.class);
	}

	@AfterClass
	@Override
	public void afterClass() throws Exception {

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
	}

	@Test
	public void workflowWithoutNode() throws Exception {
		Workflow workflow = new Workflow();
		workflow.setName("Test");
		workflow.isProject = true;
		workflow.setStatus(RunStatus.STARTED);
		workflow.save();

		Task task = new Task(workflow.id);
		task.save();
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

		FacebookAction action = new FacebookAction();
		action.setName("Test Action for Engine");
		node.addAction(action);

		Content content = new Content(Content.Type.FACEBOOK);
		content.message = String.format("Oh yes!!! The damn thing works! %s", content.id);
		content.actionId = action.id;
		action.setContent(content);

		workflow.setStartNode(node);

		Task task = new Task(workflow.id);

		workflow.wire();

		action.save();
		content.save();
		node.save();
		workflow.save();
		task.save();
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

		FacebookAction action = new FacebookAction();
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
		List<BasicEntity> entities = new ArrayList<>();

		Workflow workflow = new Workflow();
		entities.add(workflow);
		workflow.setTenant(reusedTenant);
		workflow.isProject = true;
//		workflow.setStatus(RunStatus.STARTED);

		Node node = new Node();
		entities.add(node);
		workflow.addNode(node);

		FacebookAction action1 = new FacebookAction();
		entities.add(action1);
		action1.setName("Test Action for Engine");
		node.addAction(action1);

		Content facebookContent1 = new Content(Content.Type.FACEBOOK);
		entities.add(facebookContent1);
		facebookContent1.message = String.format("First!! %s", facebookContent1.id);
		action1.setContent(facebookContent1);

		FacebookAction action2 = new FacebookAction();
		entities.add(action2);
		action2.setName("Test Action for Engine");
		node.addAction(action2);

		Content facebookContent2 = new Content(Content.Type.FACEBOOK);
		entities.add(facebookContent2);
		facebookContent2.message = String.format("Second!! %s", facebookContent2.id);
		action2.setContent(facebookContent2);

		Task task = new Task(workflow.id);

		workflow.wire();

		for (BasicEntity entity : entities) {
			entity.setName("Engine test");
			entity.save();
		}
		task.save();
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
		action.setTrackEvent(new Event(DateTime.now(), Seconds.seconds(5).toStandardDuration()));
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
		Assert.assertEquals(workflow.getTrackedActions().size(), 1);

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
}
