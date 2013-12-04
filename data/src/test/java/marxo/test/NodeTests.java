package marxo.test;

import marxo.entity.action.Action;
import marxo.entity.action.PostFacebookAction;
import marxo.entity.node.Node;
import marxo.entity.user.Tenant;
import org.testng.Assert;
import org.testng.annotations.Test;

@SuppressWarnings({"uncheck", "unchecked"})
public class NodeTests extends BasicDataTests {
	@Test
	public void wireNode() throws Exception {
		Node node = new Node();
		Action action1 = new PostFacebookAction();
		Action action2 = new PostFacebookAction();
		node.addAction(action1);
		node.addAction(action2);

		Assert.assertEquals(node.getActions().size(), 2);

		Assert.assertEquals(node.getCurrentActionId(), action1.id);
		Assert.assertEquals(node.getCurrentAction().getNextAction().id, action2.id);
	}

	@Test
	public void addActions() throws Exception {
		Tenant tenant = new Tenant();

		Node node = new Node();
		node.setTenant(tenant);

		for (int i = 0; i < 3; i++) {
			Action action = new Action();
			action.setName("Test");
			node.addAction(action);
		}

		insertEntities(
				tenant,
				node
		);
		insertEntities(node.getActions());

		node = Node.get(node.id);

		Assert.assertNotNull(node.getCurrentAction());
		Assert.assertEquals(node.getCurrentAction().id, node.getActions().get(0).id);

		Assert.assertEquals(node.getCurrentAction().getNextAction().id, node.getActions().get(1).id);
		Assert.assertEquals(node.getCurrentAction().getNextAction().getNextAction().id, node.getActions().get(2).id);
		Assert.assertNull(node.getCurrentAction().getNextAction().getNextAction().getNextAction());

		for (Action action : node.getActions()) {
			Assert.assertEquals(action.tenantId, tenant.id);
		}
	}
}
