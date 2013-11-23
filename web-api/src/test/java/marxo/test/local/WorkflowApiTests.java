package marxo.test.local;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.net.MediaType;
import marxo.entity.workflow.Workflow;
import marxo.test.ApiTestConfiguration;
import marxo.test.BasicApiTests;
import marxo.test.Tester;
import org.joda.time.DateTime;
import org.springframework.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

@ApiTestConfiguration
public class WorkflowApiTests extends BasicApiTests {
	Workflow reusedWorkflow;
	List<Workflow> workflows = new ArrayList<>();

	@Test(dependsOnGroups = "authentication")
	public void getWorkflows() throws Exception {
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
		}
	}

	@Test
	public void testGetWrongWorkflow() throws Exception {
		try (Tester tester = new Tester().basicAuth(email, password)) {
			tester
					.httpGet(baseUrl + "workflows/000000000000000000000000")
					.send();
			tester
					.is(HttpStatus.NOT_FOUND);
		}
	}

	@Test(dependsOnGroups = "authentication", dependsOnMethods = "getWorkflows")
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
					Assert.assertEquals(workflow.createdByUserId, user.id);
					Assert.assertEquals(workflow.modifiedByUserId, user.id);
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
			Assert.assertEquals(reusedWorkflow.createdByUserId, user.id);
			Assert.assertEquals(reusedWorkflow.modifiedByUserId, user.id);
		}
	}

	@Test(dependsOnGroups = "authentication")
	public void createWorkflow() throws Exception {
		try (Tester tester = new Tester().basicAuth(email, password)) {
			tester
					.httpPost(baseUrl + "workflows", new Workflow())
					.send();
			tester
					.isCreated()
					.matchContentType(MediaType.JSON_UTF_8);

			Workflow workflow = tester.getContent(Workflow.class);

			Assert.assertEquals(workflow.tenantId, user.tenantId);
			Assert.assertEquals(workflow.createdByUserId, user.id);
			Assert.assertEquals(workflow.modifiedByUserId, user.id);

			DateTime now = DateTime.now();
			Assert.assertEquals(workflow.createdDate.dayOfYear().get(), now.dayOfYear().get());
			Assert.assertEquals(workflow.modifiedDate.dayOfYear().get(), now.dayOfYear().get());
		}
	}

	@Test(dependsOnGroups = "authentication")
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

	@Test(dependsOnGroups = "authentication")
	public void getSharedWorkflows() throws Exception {
		Workflow sharedWorkflow = new Workflow();
		workflowsToBeRemoved.add(sharedWorkflow);
		sharedWorkflow.setName("Shared workflow");
		sharedWorkflow.createdByUserId = sharedWorkflow.modifiedByUserId = user.id;
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
