package marxo.test.local;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.net.MediaType;
import marxo.test.ApiTestConfiguration;
import marxo.test.BasicApiTests;
import marxo.test.Tester;
import marxo.entity.content.FacebookContent;
import marxo.entity.link.Link;
import marxo.entity.node.Event;
import marxo.entity.node.Node;
import marxo.entity.node.PostFacebook;
import marxo.entity.user.Tenant;
import marxo.entity.user.User;
import marxo.entity.workflow.Workflow;
import marxo.exception.ErrorJson;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

@SuppressWarnings("unchecked")
@ApiTestConfiguration
public class LocalApiTests extends BasicApiTests {
	Workflow reusedWorkflow;

	/*
	Authentication
	 */

	@Test(groups = "authentication")
	public void getUser() throws Exception {
		try (Tester tester = new Tester().basicAuth(email, password)) {
			tester
					.httpGet(baseUrl + "users/" + email)
					.send();
			tester
					.isOk()
					.matchContentType(MediaType.JSON_UTF_8);
			user = tester.getContent(User.class);
			Assert.assertEquals(user.getEmail(), email);
			Assert.assertNotNull(user.id);
			Assert.assertNotNull(user.getName());
			Assert.assertNotNull(user.createdDate);
			Assert.assertNotNull(user.modifiedDate);
			Assert.assertNotNull(user.tenantId);
			Assert.assertNull(user.getPassword());
		}
	}

	@Test
	public void wrongAuthentication() throws Exception {
		try (Tester tester = new Tester()) {
			tester
					.httpGet(baseUrl)
					.basicAuth(email, "wrong password")
					.send();
			tester
					.is(HttpStatus.UNAUTHORIZED)
					.matchContentType(MediaType.JSON_UTF_8);
			ErrorJson errorJson = tester.getContent(ErrorJson.class);
			Assert.assertNotNull(errorJson);

			tester
					.httpGet(baseUrl)
					.basicAuth("wrong email", password)
					.send();
			tester
					.is(HttpStatus.UNAUTHORIZED)
					.matchContentType(MediaType.JSON_UTF_8);
			errorJson = tester.getContent(ErrorJson.class);
			Assert.assertNotNull(errorJson);

			tester
					.httpGet(baseUrl)
					.basicAuth("", "")
					.send();
			tester
					.is(HttpStatus.UNAUTHORIZED)
					.matchContentType(MediaType.JSON_UTF_8);
			errorJson = tester.getContent(ErrorJson.class);
			Assert.assertNotNull(errorJson);
		}
	}

	@Test(dependsOnGroups = "authentication")
	public void getUsers() throws Exception {
		try (Tester tester = new Tester().basicAuth(email, password)) {
			tester
					.httpGet(baseUrl + "users")
					.send();
			tester
					.isOk()
					.matchContentType(MediaType.JSON_UTF_8);

			List<User> users = tester.getContent(new TypeReference<List<User>>() {
			});
			Assert.assertNotNull(users);
			for (User user : users) {
				Assert.assertEquals(user.tenantId, this.user.tenantId);
			}
		}
	}

	@Test(dependsOnGroups = "authentication")
	public void testGetTenants() throws Exception {
		try (Tester tester = new Tester().basicAuth(email, password)) {
			tester
					.httpGet(baseUrl + "tenants")
					.send();
			tester
					.isOk()
					.matchContentType(MediaType.JSON_UTF_8);

			List<Tenant> tenants = tester.getContent(new TypeReference<List<Tenant>>() {
			});
			Assert.assertNotNull(tenants);
			boolean doesContainThisUser = false;
			for (Tenant tenant : tenants) {
				if (tenant.id.equals(user.tenantId)) {
					doesContainThisUser = true;
					break;
				}
			}
			Assert.assertTrue(doesContainThisUser);
		}
	}

	/*
	General
	 */

	@Test(dependsOnGroups = "authentication")
	public void createBadEntity() throws Exception {
		try (Tester tester = new Tester().basicAuth(email, password)) {
			tester
					.httpPost(baseUrl + "workflows", "1{}2")
					.send();
			tester
					.isBadRequest();
		}
	}

	@Test
	public void testWiring() throws Exception {
		Workflow workflow = new Workflow();
		workflowsToBeRemoved.add(workflow);

		Node node1 = new Node();
		nodesToBeRemoved.add(node1);
		workflow.setStartNode(node1);
		node1.setWorkflow(workflow);

		PostFacebook action = new PostFacebook();
		action.setNode(node1);
		action.setEvent(new Event());

		FacebookContent content = new FacebookContent();
		contentsToBeRemoved.add(content);
		action.setContent(content);
		content.message = "Action run by Marxo Engine";

		Link link = new Link();
		linksToBeRemoved.add(link);
		link.setWorkflow(workflow);
		link.setPreviousNode(node1);

		Node node2 = new Node();
		nodesToBeRemoved.add(node2);
		workflow.setStartNode(node2);
		link.setNextNode(node2);
		node2.setWorkflow(workflow);

		try (Tester tester = new Tester().basicAuth(email, password)) {
			tester
					.httpPost(baseUrl + "workflows", workflow)
					.send();
			tester
					.isCreated();

			tester
					.httpPost(baseUrl + "node", node1)
					.send();
			tester
					.isCreated();

			tester
					.httpPost(baseUrl + "content", content)
					.send();
			tester
					.isCreated();

			tester
					.httpPost(baseUrl + "link", link)
					.send();
			tester
					.isCreated();

			tester
					.httpPost(baseUrl + "node", node2)
					.send();
			tester
					.isCreated();


		}
	}

	/*
	Workflow
	 */

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

	/*
	Node
	 */

	@Test(dependsOnGroups = "authentication", dependsOnMethods = "searchWorkflows")
	public void getNodes() throws Exception {
		try (Tester tester = new Tester().basicAuth(email, password)) {
			tester
					.httpGet(baseUrl + "workflow/" + reusedWorkflow.id + "/node")
					.send();
			tester
					.isOk()
					.matchContentType(MediaType.JSON_UTF_8);
			List<Node> nodes = tester.getContent(new TypeReference<List<Node>>() {
			});
			Assert.assertNotNull(nodes);
			for (Node node : nodes) {
				Assert.assertEquals(node.workflowId, reusedWorkflow.id);
//				Assert.assertEquals(node.tenantId, this.user.tenantId);
//				for (Action action : node.actions) {
//					if (action.contextId != null) {
//						Assert.assertNotNull(action.contextType);
//					}
//				}
			}
		}
	}

	@Test(dependsOnGroups = "authentication", dependsOnMethods = "searchWorkflows")
	public void getNoNode() throws Exception {
		try (Tester tester = new Tester().basicAuth(email, password)) {
			tester
					.httpGet(baseUrl + "workflow/" + reusedWorkflow.id + "/node/" + (new ObjectId()))
					.send();
			tester
					.isBadRequest()
					.matchContentType(MediaType.JSON_UTF_8);
			ErrorJson errorJson = tester.getContent(ErrorJson.class);
			Assert.assertNotNull(errorJson);
		}
	}

	/*
	Link
	 */

	@Test(dependsOnGroups = "authentication", dependsOnMethods = "searchWorkflows")
	public void getLinks() throws Exception {
		try (Tester tester = new Tester().basicAuth(email, password)) {
			tester
					.httpGet(baseUrl + "workflow/" + reusedWorkflow.id + "/links/")
					.send();
			tester
					.isOk()
					.matchContentType(MediaType.JSON_UTF_8);
			List<Link> links = tester.getContent(new TypeReference<List<Link>>() {
			});
			Assert.assertNotNull(links);
			for (Link link : links) {
				Assert.assertEquals(link.workflowId, reusedWorkflow.id);
//				Assert.assertEquals(link.tenantId, this.user.tenantId);
//				if (link.condition != null) {
//					Assert.assertNotNull(link.condition.leftOperand);
//					Assert.assertNotNull(link.condition.leftOperandType);
//					Assert.assertNotNull(link.condition.rightOperand);
//					Assert.assertNotNull(link.condition.rightOperandType);
//				}
			}
		}
	}
}