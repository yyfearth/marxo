package marxo.test;

import marxo.entity.action.Action;
import marxo.entity.action.TriggerAction;
import org.bson.types.ObjectId;
import org.testng.Assert;
import org.testng.annotations.Test;

@SuppressWarnings({"uncheck", "unchecked"})
public class ActionTests extends BasicDataTests {

	@Test
	public void getAction() throws Exception {
		Action postFacebookAction = new TriggerAction();

		insertEntities(postFacebookAction);

		Action action = Action.get(postFacebookAction.id);
		Assert.assertEquals(action.id, postFacebookAction.id);
	}

	@Test
	public void getWrongAction() throws Exception {
		Action action = Action.get(new ObjectId());
		Assert.assertNull(action);
	}

	@Test
	public void saveAction() throws Exception {
		Action postFacebookAction = new TriggerAction();

		postFacebookAction.setName("Hello world");

		insertEntities(postFacebookAction);

		Action action = Action.get(postFacebookAction.id);
		Assert.assertEquals(action.getName(), postFacebookAction.getName());
	}
}
