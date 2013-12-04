package marxo.test.local;

import com.google.common.net.MediaType;
import marxo.entity.action.Action;
import marxo.entity.action.Action;
import marxo.entity.node.Event;
import marxo.entity.node.Node;
import marxo.test.ApiTestConfiguration;
import marxo.test.ApiTester;
import marxo.test.BasicApiTests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@ApiTestConfiguration("http://localhost:8080/api/events/")
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
		reusedNode.tenantId = reusedUser.tenantId;

		reusedAction = new Action();
		reusedNode.addAction(reusedAction);

		insertEntities(
				reusedNode
		);
	}

	@Test
	public void createEvent() throws Exception {
		reusedEvent = new Event();
		reusedEvent.setAction(reusedAction);
		reusedEvent.setNode(reusedNode);
		entitiesToRemove.add(reusedEvent);

		try (ApiTester apiTester = new ApiTester().baseUrl(baseUrl).basicAuth(email, password)) {
			apiTester
					.httpPost(reusedEvent)
					.send();
			apiTester
					.isCreated()
					.matchContentType(MediaType.JSON_UTF_8);

			Event event = apiTester.getContent(Event.class);
			Assert.assertNotNull(event);
			Assert.assertEquals(event.id, reusedEvent.id);
			Assert.assertEquals(event.actionId, reusedAction.id);
			Assert.assertEquals(event.nodeId, reusedNode.id);
		}

		Event event = mongoTemplate.findById(reusedEvent.id, Event.class);
		Assert.assertNotNull(event);
		Assert.assertEquals(event.id, reusedEvent.id);
		Assert.assertEquals(event.actionId, reusedAction.id);
		Assert.assertEquals(event.nodeId, reusedNode.id);
	}

	@Test(dependsOnMethods = "createEvent")
	public void readEvent() throws Exception {
		try (ApiTester apiTester = new ApiTester().baseUrl(baseUrl).basicAuth(email, password)) {
			apiTester
					.httpGet(reusedEvent.id.toString())
					.send();
			apiTester
					.isOk()
					.matchContentType(MediaType.JSON_UTF_8);

			Event event = apiTester.getContent(Event.class);
			Assert.assertEquals(event.id, reusedEvent.id);
			Assert.assertEquals(event.actionId, reusedAction.id);
			Assert.assertEquals(event.nodeId, reusedNode.id);
		}

		Event event = mongoTemplate.findById(reusedEvent.id, Event.class);
		Assert.assertNotNull(event);
		Assert.assertEquals(event.id, reusedEvent.id);
		Assert.assertEquals(event.actionId, reusedAction.id);
		Assert.assertEquals(event.nodeId, reusedNode.id);
	}

	@Test(dependsOnMethods = "readEvent")
	public void updateEvent() throws Exception {
		reusedEvent.setName("Updated event");

		try (ApiTester apiTester = new ApiTester().baseUrl(baseUrl).basicAuth(email, password)) {
			apiTester
					.httpPut(reusedEvent.id.toString(), reusedEvent)
					.send();
			apiTester
					.isOk()
					.matchContentType(MediaType.JSON_UTF_8);

			Event event = apiTester.getContent(Event.class);
			Assert.assertNotNull(event);
			Assert.assertEquals(event.id, reusedEvent.id);
			Assert.assertEquals(event.actionId, reusedAction.id);
			Assert.assertEquals(event.nodeId, reusedNode.id);
			Assert.assertEquals(event.getName(), reusedEvent.getName());
		}

		Event event = mongoTemplate.findById(reusedEvent.id, Event.class);
		Assert.assertNotNull(event);
		Assert.assertEquals(event.id, reusedEvent.id);
		Assert.assertEquals(event.actionId, reusedAction.id);
		Assert.assertEquals(event.nodeId, reusedNode.id);
	}

	@Test(dependsOnMethods = "updateEvent")
	public void deleteEvent() throws Exception {
		try (ApiTester apiTester = new ApiTester().baseUrl(baseUrl).basicAuth(email, password)) {
			apiTester
					.httpDelete(reusedEvent.id.toString())
					.send();
			apiTester
					.isOk()
					.matchContentType(MediaType.JSON_UTF_8);

			Event event = apiTester.getContent(Event.class);
			Assert.assertNotNull(event);
			Assert.assertEquals(event.id, reusedEvent.id);
			Assert.assertEquals(event.actionId, reusedAction.id);
			Assert.assertEquals(event.nodeId, reusedNode.id);
			Assert.assertEquals(event.getName(), reusedEvent.getName());
		}

		Event event = mongoTemplate.findById(reusedEvent.id, Event.class);
		Assert.assertNull(event);
	}
}
