package marxo.test.local;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.net.MediaType;
import marxo.entity.workflow.Workflow;
import marxo.test.ApiTestConfiguration;
import marxo.test.BasicApiTests;
import marxo.test.Tester;
import marxo.validation.SelectIdFunction;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@ApiTestConfiguration
@Test(groups = "workflow")
public class WorkflowApiTests extends BasicApiTests {
	Workflow reusedWorkflow;
	List<Workflow> workflows = new ArrayList<>();

	@Test
	public void createWorkflow() throws Exception {
		try (Tester tester = new Tester().basicAuth(email, password)) {
			tester
					.httpPost(baseUrl + "workflows", new Workflow())
					.send();
			tester
					.isCreated()
					.matchContentType(MediaType.JSON_UTF_8);

			reusedWorkflow = tester.getContent(Workflow.class);

			Assert.assertEquals(reusedWorkflow.tenantId, user.tenantId);
			Assert.assertEquals(reusedWorkflow.createUserId, user.id);
			Assert.assertEquals(reusedWorkflow.updateUserId, user.id);

			DateTime now = DateTime.now();
			Assert.assertEquals(reusedWorkflow.createTime.dayOfYear().get(), now.dayOfYear().get());
			Assert.assertEquals(reusedWorkflow.updateTime.dayOfYear().get(), now.dayOfYear().get());
		}
	}

	public void getAllWorkflows() throws Exception {
		try (Tester tester = new Tester().basicAuth(email, password)) {
			tester
					.httpGet(baseUrl + "workflows")
					.send();
			tester
					.isOk()
					.matchContentType(MediaType.JSON_UTF_8);

			workflows = tester.getContent(new TypeReference<List<Workflow>>() {
			});
			Assert.assertNotNull(workflows);
			for (Workflow workflow : workflows) {
				if (workflow.tenantId != null) {
					Assert.assertEquals(workflow.tenantId, this.user.tenantId);
				}
				Assert.assertFalse(workflow.isProject);
			}

			Criteria criteria = Criteria.where("isProject").is(false);
			Criteria criteria1 = Criteria.where("tenantId").is(user.tenantId);
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
		try (Tester tester = new Tester().basicAuth(email, password)) {
			tester
					.httpGet(baseUrl + "workflows/000000000000000000000000")
					.send();
			tester
					.is(HttpStatus.NOT_FOUND);
		}
	}

	@Test(dependsOnMethods = {"getAllWorkflows"})
	public void searchWorkflows() throws Exception {
		try (Tester tester = new Tester().basicAuth(email, password)) {
			if (workflows.size() == 0) {
				tester
						.httpGet(baseUrl + "workflows")
						.send();
				tester
						.isOk()
						.matchContentType(MediaType.JSON_UTF_8);

				List<Workflow> workflows = tester.getContent(new TypeReference<List<Workflow>>() {
				});
				Assert.assertNotNull(workflows);
				Assert.assertEquals(workflows.size(), 0);

				for (Workflow workflow : workflows) {
					Assert.assertEquals(workflow.nodes.size(), 0);
					Assert.assertEquals(workflow.links.size(), 0);

					if (workflow.tenantId != null) {
						Assert.assertEquals(workflow.tenantId, user.tenantId);
					}
					Assert.assertEquals(workflow.createUserId, user.id);
					Assert.assertEquals(workflow.updateUserId, user.id);
				}
			}

			reusedWorkflow = workflows.get(0);

			tester
					.httpGet(baseUrl + "workflows/" + reusedWorkflow.id)
					.send();
			tester
					.isOk()
					.matchContentType(MediaType.JSON_UTF_8);

			reusedWorkflow = tester.getContent(Workflow.class);
			Assert.assertNotNull(reusedWorkflow);
			Assert.assertEquals(reusedWorkflow.nodeIds.size(), reusedWorkflow.nodes.size());
			Assert.assertEquals(reusedWorkflow.linkIds.size(), reusedWorkflow.links.size());

			if (reusedWorkflow.tenantId != null) {
				Assert.assertEquals(reusedWorkflow.tenantId, user.tenantId);
			}
			Assert.assertEquals(reusedWorkflow.createUserId, user.id);
			Assert.assertEquals(reusedWorkflow.updateUserId, user.id);
		}
	}

	@Test
	public void getProjects() throws Exception {
		try (Tester tester = new Tester().basicAuth(email, password)) {
			tester
					.httpGet(baseUrl + "projects")
					.send();
			tester
					.isOk()
					.matchContentType(MediaType.JSON_UTF_8);

			List<Workflow> workflows = tester.getContent(new TypeReference<List<Workflow>>() {
			});
			Assert.assertNotNull(workflows);
			for (Workflow workflow : workflows) {
				Assert.assertEquals(workflow.tenantId, this.user.tenantId);
				Assert.assertTrue(workflow.isProject);
			}
		}
	}

	@Test
	public void getSharedWorkflows() throws Exception {
		Workflow sharedWorkflow = new Workflow();
		workflowsToBeRemoved.add(sharedWorkflow);
		sharedWorkflow.setName("Shared workflow");
		sharedWorkflow.createUserId = sharedWorkflow.updateUserId = user.id;
		mongoTemplate.insert(sharedWorkflow);

		try (Tester tester = new Tester().basicAuth(email, password)) {
			tester
					.httpGet(baseUrl + "workflow/" + sharedWorkflow.id)
					.send();
			tester
					.isOk()
					.matchContentType(MediaType.JSON_UTF_8);

			{
				Workflow workflow = tester.getContent(Workflow.class);
				Assert.assertNull(workflow.tenantId);
			}

			tester
					.httpGet(baseUrl + "workflows")
					.send();
			tester
					.isOk()
					.matchContentType(MediaType.JSON_UTF_8);

			List<Workflow> workflows = tester.getContent(new TypeReference<List<Workflow>>() {
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
}
