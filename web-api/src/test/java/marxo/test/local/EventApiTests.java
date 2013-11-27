package marxo.test.local;

import com.google.common.net.MediaType;
import marxo.entity.node.Action;
import marxo.entity.node.Event;
import marxo.entity.node.Node;
import marxo.entity.node.PostFacebook;
import marxo.test.ApiTestConfiguration;
import marxo.test.BasicApiTests;
import marxo.test.Tester;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@ApiTestConfiguration("http://localhost:8080/marxo/api/events/")
public class EventApiTests extends BasicApiTests {
	Node reusedNode;
	Action reusedAction;
	Event reusedEvent;

	@BeforeClass
	@Override
	public void beforeClass() throws Exception {
		super.beforeClass();

		reusedNode = new Node();
		reusedNode.setName(getClass().getSimpleName());
		entitiesToInsert.add(reusedNode);

		reusedAction = new PostFacebook();
		reusedNode.addAction(reusedAction);
		entitiesToInsert.add(reusedNode);

		insertEntities();
	}

	@Test
	public void createEvent() throws Exception {
		reusedEvent = new Event();
		reusedEvent.setAction(reusedAction);
		reusedEvent.setNode(reusedNode);
		entitiesToInsert.add(reusedEvent);

		try (Tester tester = new Tester().basicAuth(email, password)) {
			tester
					.httpPost(reusedEvent)
					.send();
			tester
					.isCreated()
					.matchContentType(MediaType.JSON_UTF_8);

			Event event = tester.getContent(Event.class);
			Assert.assertEquals(event.id, reusedEvent.id);
			Assert.assertEquals(event.actionId, reusedEvent.actionId);
			Assert.assertEquals(event.getNodeId(), reusedEvent.getNodeId());
		}

		Node node = Node.get(reusedNode.id);
		Event event = node.getActionMap().get(reusedAction.id).getEvent();

	}

	@Test(dependsOnMethods = "createEvent")
	public void readEvent() throws Exception {
		try (Tester tester = new Tester().basicAuth(email, password)) {
			tester
					.httpGet(reusedEvent.id.toString())
					.send();
			tester
					.isOk()
					.matchContentType(MediaType.JSON_UTF_8);

			Event event = tester.getContent(Event.class);
			Assert.assertEquals(event.id, reusedEvent.id);
			Assert.assertEquals(event.getName(), reusedEvent.getName());
		}
	}

	@Test(dependsOnMethods = "readEvent")
	public void updateEvent() throws Exception {
		try (Tester tester = new Tester().basicAuth(email, password)) {
			reusedEvent.setName("Updated event");

			tester
					.httpPut(reusedEvent.id.toString(), reusedEvent)
					.send();
			tester
					.isOk()
					.matchContentType(MediaType.JSON_UTF_8);

			Event event = tester.getContent(Event.class);
			Assert.assertEquals(event.id, reusedEvent.id);
			Assert.assertEquals(event.getName(), reusedEvent.getName());
		}

		Node node1 = mongoTemplate.findById(reusedNode.id, Node.class);
		Assert.assertEquals(node1.getActions().get(0).getEvent().getName(), reusedEvent.getName());
	}

	@Test(dependsOnMethods = "updateEvent")
	public void deleteEvent() throws Exception {
		try (Tester tester = new Tester().basicAuth(email, password)) {
			tester
					.httpDelete(reusedEvent.id.toString())
					.send();
			tester
					.isOk()
					.matchContentType(MediaType.JSON_UTF_8);
		}

		Node node1 = mongoTemplate.findById(reusedNode.id, Node.class);
		Assert.assertNull(node1.getActions().get(0).getEvent());
	}
}
