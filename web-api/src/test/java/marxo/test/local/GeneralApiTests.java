package marxo.test.local;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.net.MediaType;
import marxo.entity.action.PostFacebookAction;
import marxo.entity.content.FacebookContent;
import marxo.entity.link.Link;
import marxo.entity.node.Node;
import marxo.entity.workflow.Workflow;
import marxo.exception.ErrorJson;
import marxo.test.ApiTestConfiguration;
import marxo.test.ApiTester;
import marxo.test.BasicApiTests;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

@SuppressWarnings("unchecked")
@ApiTestConfiguration
public class GeneralApiTests extends BasicApiTests {
	Workflow reusedWorkflow;
	Node reusedNode;

	@BeforeClass
	@Override
	public void beforeClass() throws Exception {
		super.beforeClass();

		reusedWorkflow = new Workflow();
		reusedWorkflow.createUserId = reusedWorkflow.updateUserId = reusedUser.id;
		reusedWorkflow.tenantId = reusedUser.tenantId;
		reusedWorkflow.setName(getClass().getSimpleName());

		reusedNode = new Node();
		reusedNode.setWorkflow(reusedWorkflow);
		reusedNode.createUserId = reusedNode.updateUserId = reusedUser.id;
		reusedWorkflow.setName(getClass().getSimpleName());

		insertEntities(
				reusedWorkflow,
				reusedNode
		);
	}

	@Test
	public void createBadEntity() throws Exception {
		try (ApiTester apiTester = new ApiTester().basicAuth(email, password)) {
			apiTester
					.httpPost(baseUrl + "workflows", "1{}2")
					.send();
			apiTester
					.isBadRequest();
		}
	}

	@Test
	public void wiring() throws Exception {
		Workflow workflow = new Workflow();
		entitiesToRemove.add(workflow);

		Node node1 = new Node();
		entitiesToRemove.add(node1);
		workflow.addNode(node1);

		PostFacebookAction action = new PostFacebookAction();
		node1.addAction(action);

		FacebookContent content = new FacebookContent();
		content.message = "Action run by Marxo Engine";
		entitiesToRemove.add(content);
		action.setContent(content);

		Link link = new Link();
		link.setPreviousNode(node1);
		entitiesToRemove.add(link);
		workflow.addLink(link);

		Node node2 = new Node();
		entitiesToRemove.add(node2);
		link.setNextNode(node2);
		workflow.addNode(node2);

		try (ApiTester apiTester = apiTesterBuilder.build()) {
			apiTester
					.httpPost(baseUrl + "workflows", workflow)
					.send();
			apiTester
					.isCreated();

			apiTester
					.httpPost(baseUrl + "node", node1)
					.send();
			apiTester
					.isCreated();

			apiTester
					.httpPost(baseUrl + "content", content)
					.send();
			apiTester
					.isCreated();

			apiTester
					.httpPost(baseUrl + "link", link)
					.send();
			apiTester
					.isCreated();

			apiTester
					.httpPost(baseUrl + "node", node2)
					.send();
			apiTester
					.isCreated();
		}
	}

	/*
	Node
	 */

	@Test
	public void getNodes() throws Exception {
		try (ApiTester apiTester = new ApiTester().basicAuth(email, password)) {
			apiTester
					.httpGet(baseUrl + "workflow/" + reusedWorkflow.id + "/node")
					.send();
			apiTester
					.isOk()
					.matchContentType(MediaType.JSON_UTF_8);
			List<Node> nodes = apiTester.getContent(new TypeReference<List<Node>>() {
			});
			Assert.assertNotNull(nodes);
			for (Node node : nodes) {
				Assert.assertEquals(node.workflowId, reusedWorkflow.id);
			}
		}
	}

	@Test
	public void wrongSubResource() throws Exception {
		try (ApiTester apiTester = new ApiTester().basicAuth(email, password)) {
			apiTester
					.httpGet(baseUrl + "workflow/" + reusedWorkflow.id + "/node/" + (new ObjectId()))
					.send();
			apiTester
					.is(HttpStatus.NOT_FOUND)
					.matchContentType(MediaType.JSON_UTF_8);
			ErrorJson errorJson = apiTester.getContent(ErrorJson.class);
			Assert.assertNotNull(errorJson);
		}
	}

	@Test
	public void rightSubResource() throws Exception {
		try (ApiTester apiTester = new ApiTester().basicAuth(email, password)) {
			apiTester
					.httpGet(baseUrl + "workflow/" + reusedNode.workflowId + "/node/" + reusedNode.id)
					.send();
			apiTester
					.isOk()
					.matchContentType(MediaType.JSON_UTF_8);
			Node node = apiTester.getContent(Node.class);
			Assert.assertEquals(node.id, reusedNode.id);
		}
	}

	/*
	Link
	 */

	@Test
	public void getLinks() throws Exception {
		try (ApiTester apiTester = new ApiTester().basicAuth(email, password)) {
			apiTester
					.httpGet(baseUrl + "workflow/" + reusedWorkflow.id + "/links/")
					.send();
			apiTester
					.isOk()
					.matchContentType(MediaType.JSON_UTF_8);
			List<Link> links = apiTester.getContent(new TypeReference<List<Link>>() {
			});
			Assert.assertNotNull(links);
			for (Link link : links) {
				Assert.assertEquals(link.workflowId, reusedWorkflow.id);
			}
		}
	}
}