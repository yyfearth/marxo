package marxo.test.local;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.net.MediaType;
import marxo.entity.content.FacebookContent;
import marxo.entity.link.Link;
import marxo.entity.node.Event;
import marxo.entity.node.Node;
import marxo.entity.node.PostFacebookAction;
import marxo.entity.workflow.Workflow;
import marxo.exception.ErrorJson;
import marxo.test.ApiTestConfiguration;
import marxo.test.BasicApiTests;
import marxo.test.Tester;
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
		try (Tester tester = new Tester().basicAuth(email, password)) {
			tester
					.httpPost(baseUrl + "workflows", "1{}2")
					.send();
			tester
					.isBadRequest();
		}
	}

	@Test
	public void wiring() throws Exception {
		Workflow workflow = new Workflow();
		entitiesToRemove.add(workflow);

		Node node1 = new Node();
		entitiesToRemove.add(node1);
		workflow.setStartNode(node1);
		node1.setWorkflow(workflow);

		PostFacebookAction action = new PostFacebookAction();
		action.setNode(node1);
		action.setEvent(new Event());

		FacebookContent content = new FacebookContent();
		entitiesToRemove.add(content);
		action.setContent(content);
		content.message = "Action run by Marxo Engine";

		Link link = new Link();
		entitiesToRemove.add(link);
		link.setWorkflow(workflow);
		link.setPreviousNode(node1);

		Node node2 = new Node();
		entitiesToRemove.add(node2);
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
	Node
	 */

	@Test
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

	@Test
	public void wrongSubResource() throws Exception {
		try (Tester tester = new Tester().basicAuth(email, password)) {
			tester
					.httpGet(baseUrl + "workflow/" + reusedWorkflow.id + "/node/" + (new ObjectId()))
					.send();
			tester
					.is(HttpStatus.NOT_FOUND)
					.matchContentType(MediaType.JSON_UTF_8);
			ErrorJson errorJson = tester.getContent(ErrorJson.class);
			Assert.assertNotNull(errorJson);
		}
	}

	@Test
	public void rightSubResource() throws Exception {
		try (Tester tester = new Tester().basicAuth(email, password)) {
			tester
					.httpGet(baseUrl + "workflow/" + reusedNode.workflowId + "/node/" + reusedNode.id)
					.send();
			tester
					.isOk()
					.matchContentType(MediaType.JSON_UTF_8);
			Node node = tester.getContent(Node.class);
			Assert.assertEquals(node.id, reusedNode.id);
		}
	}

	/*
	Link
	 */

	@Test
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