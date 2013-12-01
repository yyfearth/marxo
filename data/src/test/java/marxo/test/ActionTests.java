package marxo.test;

import marxo.entity.action.Action;
import marxo.entity.action.PostFacebookAction;
import marxo.entity.action.WaitUserAction;
import marxo.entity.node.Node;
import org.bson.types.ObjectId;
import org.testng.Assert;
import org.testng.annotations.Test;

@SuppressWarnings({"uncheck", "unchecked"})
public class ActionTests extends BasicDataTests {

	@Test
	public void getAction() throws Exception {
		PostFacebookAction postFacebookAction = new PostFacebookAction();

		Node node = new Node();
		node.addAction(postFacebookAction);

		insertEntities(node);

		Action action = Action.get(postFacebookAction.id);
		Assert.assertTrue(action instanceof PostFacebookAction);
		Assert.assertEquals(action.id, postFacebookAction.id);
	}

	@Test
	public void getWrongAction() throws Exception {
		Action action = Action.get(new ObjectId());
		Assert.assertNull(action);
	}

	@Test
	public void saveAction() throws Exception {
		WaitUserAction waitUserAction = new WaitUserAction();

		Node node = new Node();
		node.addAction(waitUserAction);

		insertEntities(node);

		waitUserAction.setName("Hello world");
		waitUserAction.save();

		node = Node.get(node.id);
		Action action = node.getActions().get(0);
		Assert.assertEquals(action.getName(), waitUserAction.getName());
	}
}
