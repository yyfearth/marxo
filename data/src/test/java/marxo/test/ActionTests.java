package marxo.test;

import marxo.entity.action.Action;
import marxo.entity.action.PostFacebookAction;
import org.bson.types.ObjectId;
import org.testng.Assert;
import org.testng.annotations.Test;

@SuppressWarnings({"uncheck", "unchecked"})
public class ActionTests extends BasicDataTests {

	@Test
	public void getAction() throws Exception {
		PostFacebookAction postFacebookAction = new PostFacebookAction();

		insertEntities(postFacebookAction);

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
		PostFacebookAction postFacebookAction = new PostFacebookAction();

		postFacebookAction.setName("Hello world");

		insertEntities(postFacebookAction);

		Action action = Action.get(postFacebookAction.id);
		Assert.assertEquals(action.getName(), postFacebookAction.getName());
	}
}
