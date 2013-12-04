package marxo.test.local;

import com.google.common.net.MediaType;
import marxo.entity.action.Action;
import marxo.entity.node.Node;
import marxo.test.ApiTestConfiguration;
import marxo.test.ApiTester;
import marxo.test.BasicApiTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

@ApiTestConfiguration("http://localhost:8080/api/nodes/")
public class NodeApiTests extends BasicApiTests {
	Node reusedNode;

	@Test
	public void createNode() throws Exception {
		reusedNode = new Node();
		reusedNode.setName("Test");

		List<Action> postFacebookActions = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			Action postFacebookAction = new Action();
			postFacebookAction.setName("Test");
			postFacebookActions.add(postFacebookAction);
			reusedNode.addAction(postFacebookAction);
		}

		entitiesToRemove.add(reusedNode);
		entitiesToRemove.addAll(postFacebookActions);

		try (ApiTester apiTester = apiTesterBuilder.build()) {

			apiTester
					.httpPost(reusedNode)
					.send();
			apiTester
					.isCreated()
					.matchContentType(MediaType.JSON_UTF_8);

			Node node = apiTester.getContent(Node.class);
			Assert.assertNotNull(node);
			Assert.assertEquals(node.getName(), "Test");
			Assert.assertEquals(node.actionIds.size(), 3);
		}

		Node node = Node.get(reusedNode.id);
		Assert.assertEquals(node.getName(), "Test");
		Assert.assertEquals(node.getActions().size(), 3);
	}

	@Test(dependsOnMethods = "createNode")
	public void readNode() throws Exception {
		try (ApiTester apiTester = apiTesterBuilder.build()) {
			apiTester
					.httpGet(reusedNode.id.toString())
					.send();
			apiTester
					.isOk()
					.matchContentType(MediaType.JSON_UTF_8);
			Node node = apiTester.getContent(Node.class);
			Assert.assertEquals(node.id, reusedNode.id);
		}
	}

	@Test(dependsOnMethods = "readNode")
	public void updateNode() throws Exception {
		reusedNode.setName("Updated");

		try (ApiTester apiTester = apiTesterBuilder.build()) {
			apiTester
					.httpPut(reusedNode.id.toString(), reusedNode)
					.send();
			apiTester
					.isOk()
					.matchContentType(MediaType.JSON_UTF_8);
			Node node = apiTester.getContent(Node.class);
			Assert.assertNotNull(node);
			Assert.assertEquals(node.getName(), reusedNode.getName());
		}

		Node node = Node.get(reusedNode.id);
		Assert.assertEquals(node.getName(), reusedNode.getName());
	}

	@Test(dependsOnMethods = "updateNode")
	public void deleteNode() throws Exception {
		try (ApiTester apiTester = apiTesterBuilder.build()) {
			apiTester
					.httpDelete(reusedNode.id.toString())
					.send();
			apiTester
					.isOk()
					.matchContentType(MediaType.JSON_UTF_8);
		}

		Node node = mongoTemplate.findById(reusedNode.id, Node.class);
		Assert.assertNull(node);
	}
}
