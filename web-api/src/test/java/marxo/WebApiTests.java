package marxo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.net.MediaType;
import marxo.entity.link.Link;
import marxo.entity.node.Node;
import marxo.entity.user.User;
import marxo.entity.workflow.Workflow;
import marxo.exception.ErrorJson;
import marxo.tool.Loggable;
import org.bson.types.ObjectId;
import org.junit.AfterClass;
import org.springframework.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public class WebApiTests implements Loggable {
	final String email = "yyfearth@gmail.com";
	final String password = "2k96H29ECsJ05BJAkEGm6FC+UgjwVTc1qOd7SGG2uS8";
	User user;
	//	String baseUrl = "http://localhost:8080/api/";
	String baseUrl = "http://masonwan.com/marxo/api/";
	List<Workflow> workflows = new ArrayList<>();
	Workflow workflow;

	@BeforeClass
	public void beforeClass() {
	}

	@AfterClass
	public void afterClass() throws IOException {
	}

	@BeforeMethod
	public void setUp() throws Exception {
	}

	@AfterMethod
	public void tearDown() throws Exception {
	}

	@Test(priority = -100, groups = "start")
	public void getUser() throws Exception {
		try (Tester tester = new Tester()) {
			tester
					.httpGet(baseUrl + "users/" + email)
					.basicAuth(email, password)
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

	@Test(priority = -100, groups = "start")
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
		}
	}

	@Test(dependsOnGroups = "start")
	public void getUsers() throws Exception {
		try (Tester tester = new Tester()) {
			tester
					.httpGet(baseUrl + "users")
					.basicAuth(email, password)
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

	@Test(dependsOnGroups = "start")
	public void getWorkflows() throws Exception {
		try (Tester tester = new Tester()) {
			tester
					.httpGet(baseUrl + "workflows")
					.basicAuth(email, password)
					.send();
			tester
					.isOk()
					.matchContentType(MediaType.JSON_UTF_8);

			workflows = tester.getContent(new TypeReference<List<Workflow>>() {
			});
			Assert.assertNotNull(workflows);
			for (Workflow workflow : workflows) {
				Assert.assertEquals(workflow.tenantId, this.user.tenantId);
				Assert.assertFalse(workflow.isProject);
			}
		}
	}

	@Test(dependsOnGroups = "start", dependsOnMethods = "getWorkflows")
	public void searchWorkflows() throws Exception {
		if (workflows.size() == 0) {
			try (Tester tester = new Tester()) {
				tester
						.httpGet(baseUrl + "workflows")
						.basicAuth(email, password)
						.send();
				tester
						.isOk()
						.matchContentType(MediaType.JSON_UTF_8);

				List<Workflow> workflows = tester.getContent(new TypeReference<List<Workflow>>() {
				});
				Assert.assertNotNull(workflows);
				Assert.assertEquals(workflows.size(), 0);
			}
		}

		workflow = workflows.get(0);

		try (Tester tester = new Tester()) {
			tester
					.httpGet(baseUrl + "workflows/" + workflow.id)
					.basicAuth(email, password)
					.send();
			tester
					.isOk()
					.matchContentType(MediaType.JSON_UTF_8);

			workflow = tester.getContent(new TypeReference<Workflow>() {
			});
			Assert.assertNotNull(workflow);
			Assert.assertEquals(workflow.nodeIds.size(), workflow.nodes.size());
			Assert.assertEquals(workflow.linkIds.size(), workflow.links.size());
			Assert.assertEquals(workflow.tenantId, this.user.tenantId);
		}
	}

	@Test(dependsOnGroups = "start")
	public void getProjects() throws Exception {
		try (Tester tester = new Tester()) {
			tester
					.httpGet(baseUrl + "projects")
					.basicAuth(email, password)
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

	@Test(dependsOnGroups = "start", dependsOnMethods = "searchWorkflows")
	public void getNodes() throws Exception {
		try (Tester tester = new Tester()) {
			tester
					.httpGet(baseUrl + "workflow/" + workflow.id + "/node")
					.basicAuth(email, password)
					.send();
			tester
					.isOk()
					.matchContentType(MediaType.JSON_UTF_8);
			List<Node> nodes = tester.getContent(new TypeReference<List<Node>>() {
			});
			Assert.assertNotNull(nodes);
			for (Node node : nodes) {
				Assert.assertEquals(node.workflowId, workflow.id);
//				Assert.assertEquals(node.tenantId, this.user.tenantId);
//				for (Action action : node.actions) {
//					if (action.contextId != null) {
//						Assert.assertNotNull(action.contextType);
//					}
//				}
			}
		}
	}

	@Test(dependsOnGroups = "start", dependsOnMethods = "searchWorkflows")
	public void getNoNode() throws Exception {
		try (Tester tester = new Tester()) {
			tester
					.httpGet(baseUrl + "workflow/" + workflow.id + "/node/" + (new ObjectId()))
					.basicAuth(email, password)
					.send();
			tester
					.isBadRequest()
					.matchContentType(MediaType.JSON_UTF_8);
			ErrorJson errorJson = tester.getContent(ErrorJson.class);
			Assert.assertNotNull(errorJson);
		}
	}

	@Test(dependsOnGroups = "start", dependsOnMethods = "searchWorkflows")
	public void getLinks() throws Exception {
		try (Tester tester = new Tester()) {
			tester
					.httpGet(baseUrl + "workflow/" + workflow.id + "/links/")
					.basicAuth(email, password)
					.send();
			tester
					.isOk()
					.matchContentType(MediaType.JSON_UTF_8);
			List<Link> links = tester.getContent(new TypeReference<List<Link>>() {
			});
			Assert.assertNotNull(links);
			for (Link link : links) {
				Assert.assertEquals(link.workflowId, workflow.id);
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

