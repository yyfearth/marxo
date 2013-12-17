package marxo.test.local;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.net.MediaType;
import marxo.entity.action.Action;
import marxo.entity.action.Content;
import marxo.entity.action.FacebookAction;
import marxo.entity.link.Link;
import marxo.entity.node.Event;
import marxo.entity.node.Node;
import marxo.entity.workflow.RunStatus;
import marxo.entity.workflow.Workflow;
import marxo.test.ApiTestConfiguration;
import marxo.test.ApiTester;
import marxo.test.BasicApiTests;
import marxo.validation.SelectIdFunction;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Minutes;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@ApiTestConfiguration
public class WorkflowApiTests extends BasicApiTests {
	Workflow workflow;
	List<Workflow> workflows = new ArrayList<>();

	@Test
	public void createWorkflow() throws Exception {
		Workflow newWorkflow = new Workflow();

		try (ApiTester apiTester = new ApiTester().basicAuth(email, password)) {
			apiTester
					.httpPost(baseUrl + "workflows", newWorkflow)
					.send();
			apiTester
					.isCreated()
					.matchContentType(MediaType.JSON_UTF_8);

			workflow = apiTester.getContent(Workflow.class);

			Assert.assertEquals(workflow.tenantId, reusedUser.tenantId);
			Assert.assertEquals(workflow.createUserId, reusedUser.id);
			Assert.assertEquals(workflow.updateUserId, reusedUser.id);

			DateTime now = DateTime.now();
			Assert.assertEquals(workflow.createTime.dayOfYear().get(), now.dayOfYear().get());
			Assert.assertEquals(workflow.updateTime.dayOfYear().get(), now.dayOfYear().get());
		}
	}

	@Test
	public void getAllWorkflows() throws Exception {
		try (ApiTester apiTester = new ApiTester().basicAuth(email, password)) {
			apiTester
					.httpGet(baseUrl + "workflows")
					.send();
			apiTester
					.isOk()
					.matchContentType(MediaType.JSON_UTF_8);

			workflows = apiTester.getContent(new TypeReference<List<Workflow>>() {
			});
			Assert.assertNotNull(workflows);
			for (Workflow workflow : workflows) {
				if (workflow.tenantId != null) {
					Assert.assertEquals(workflow.tenantId, this.reusedUser.tenantId);
				}
				Assert.assertFalse(workflow.isProject);
			}

			Criteria criteria = Criteria.where("isProject").is(false);
			Criteria criteria1 = Criteria.where("tenantId").is(reusedUser.tenantId);
			Criteria criteria2 = Criteria.where("tenantId").exists(false);
			criteria.orOperator(criteria1, criteria2);

			Query query = Query.query(criteria);
			query.fields().include("id");
			List<Workflow> workflows1 = mongoTemplate.find(query, Workflow.class);
			List<ObjectId> ids1 = Lists.transform(workflows, SelectIdFunction.getInstance());
			Set<ObjectId> set1 = ImmutableSet.copyOf(ids1);
			List<ObjectId> ids2 = Lists.transform(workflows1, SelectIdFunction.getInstance());
			Set<ObjectId> set2 = ImmutableSet.copyOf(ids2);
			Assert.assertEquals(set1, set2);
		}
	}

	@Test
	public void getWrongWorkflow() throws Exception {
		try (ApiTester apiTester = new ApiTester().basicAuth(email, password)) {
			apiTester
					.httpGet(baseUrl + "workflows/000000000000000000000000")
					.send();
			apiTester
					.is(HttpStatus.NOT_FOUND);
		}
	}

	@Test(dependsOnMethods = {"getAllWorkflows"})
	public void searchWorkflows() throws Exception {
		Workflow workflow = new Workflow();
		workflow.setName("searchWorkflows");
		workflow.createUserId = workflow.updateUserId = reusedUser.id;
		workflow.save();
		entitiesToRemove.add(workflow);

		try (ApiTester apiTester = new ApiTester().basicAuth(email, password)) {
			apiTester
					.httpGet(baseUrl + "workflows")
					.send();
			apiTester
					.isOk()
					.matchContentType(MediaType.JSON_UTF_8);

			List<Workflow> workflows = apiTester.getContent(new TypeReference<List<Workflow>>() {
			});
			Assert.assertNotNull(workflows);

			boolean doesContain = false;
			for (Workflow w : workflows) {
				if (w.id.equals(workflow.id)) {
					doesContain = true;
					break;
				}
			}
			Assert.assertTrue(doesContain);

			apiTester
					.httpGet(baseUrl + "workflows/" + workflow.id)
					.send();
			apiTester
					.isOk()
					.matchContentType(MediaType.JSON_UTF_8);

			workflow = apiTester.getContent(Workflow.class);
			Assert.assertNotNull(workflow);
			Assert.assertEquals(workflow.nodeIds.size(), workflow.getNodes().size());
			Assert.assertEquals(workflow.linkIds.size(), workflow.getLinks().size());

			if (workflow.tenantId != null) {
				Assert.assertEquals(workflow.tenantId, reusedUser.tenantId);
			}
			Assert.assertEquals(workflow.createUserId, reusedUser.id);
			Assert.assertEquals(workflow.updateUserId, reusedUser.id);
		}
	}

	@Test
	public void getProjects() throws Exception {
		try (ApiTester apiTester = new ApiTester().basicAuth(email, password)) {
			apiTester
					.httpGet(baseUrl + "projects")
					.send();
			apiTester
					.isOk()
					.matchContentType(MediaType.JSON_UTF_8);

			List<Workflow> workflows = apiTester.getContent(new TypeReference<List<Workflow>>() {
			});
			Assert.assertNotNull(workflows);
			for (Workflow workflow : workflows) {
				if (workflow.tenantId != null) {
					Assert.assertEquals(workflow.tenantId, this.reusedUser.tenantId);
				}
				Assert.assertTrue(workflow.isProject);
			}
		}
	}

	@Test
	public void getSharedWorkflows() throws Exception {
		Workflow sharedWorkflow = new Workflow();
		entitiesToRemove.add(sharedWorkflow);
		sharedWorkflow.setName("Shared workflow");
		sharedWorkflow.createUserId = sharedWorkflow.updateUserId = reusedUser.id;
		mongoTemplate.insert(sharedWorkflow);

		try (ApiTester apiTester = new ApiTester().basicAuth(email, password)) {
			apiTester
					.httpGet(baseUrl + "workflow/" + sharedWorkflow.id)
					.send();
			apiTester
					.isOk()
					.matchContentType(MediaType.JSON_UTF_8);

			{
				Workflow workflow = apiTester.getContent(Workflow.class);
				Assert.assertNull(workflow.tenantId);
			}

			apiTester
					.httpGet(baseUrl + "workflows")
					.send();
			apiTester
					.isOk()
					.matchContentType(MediaType.JSON_UTF_8);

			List<Workflow> workflows = apiTester.getContent(new TypeReference<List<Workflow>>() {
			});
			Assert.assertNotNull(workflows);
			boolean doesContainSharedWorkflow = false;
			for (Workflow workflow : workflows) {
				if (workflow.id.equals(sharedWorkflow.id)) {
					doesContainSharedWorkflow = true;
					break;
				}
			}
			Assert.assertTrue(doesContainSharedWorkflow);
		}
	}

	@Test(dependsOnMethods = {"createWorkflow"})
	public void updateStatus() throws Exception {
		try (ApiTester apiTester = apiTesterBuilder.build()) {
			apiTester
					.httpPut("workflow/" + workflow.id + "/status", RunStatus.STARTED)
					.send();
			apiTester
					.isOk();
		}

		Workflow workflow1 = mongoTemplate.findOne(Query.query(Criteria.where("_id").is(workflow.id)), Workflow.class);
		Assert.assertEquals(workflow1.getStatus(), RunStatus.STARTED);
	}

	@Test
	public void deleteWorkflow() throws Exception {
		Workflow workflow1 = new Workflow();
		workflow1.setTenant(reusedTenant);

		Node node1 = new Node();
		workflow1.addNode(node1);

		FacebookAction facebookAction = new FacebookAction();
		node1.addAction(facebookAction);

		Content content = new Content(Content.Type.FACEBOOK);
		facebookAction.setContent(content);

		Event event = new Event();
		event.setStartTime(DateTime.now().plus(Minutes.minutes(5)));
		event.setDuration(Days.days(1).toStandardDuration());
		facebookAction.setEvent(event);

		Event event1 = new Event();
		event1.setStartTime(DateTime.now().plus(Minutes.minutes(5)));
		event1.setDuration(Days.days(1).toStandardDuration());
		facebookAction.setTrackEvent(event1);

		Node node2 = new Node();
		workflow1.addNode(node2);

		Link link = new Link();
		link.setPreviousNode(node1);
		link.setNextNode(node2);
		workflow1.addLink(link);

		workflow1.wire();

		mongoTemplate.insertAll(Lists.newArrayList(
				workflow1,
				node1,
				facebookAction,
				content,
				event,
				event1,
				node2,
				link
		));

		try (ApiTester apiTester = apiTesterBuilder.build()) {
			apiTester
					.httpDelete(String.format("workflow/%s", workflow1.id))
					.send();
			apiTester
					.isOk();
		}

		Assert.assertNull(Workflow.get(workflow1.id));
		Assert.assertNull(Node.get(node1.id));
		Assert.assertNull(Action.get(facebookAction.id));
		Assert.assertNull(Content.get(content.id));
		Assert.assertNull(Event.get(event.id));
		Assert.assertNull(Event.get(event1.id));
		Assert.assertNull(Node.get(node2.id));
		Assert.assertNull(Link.get(link.id));
	}
}
