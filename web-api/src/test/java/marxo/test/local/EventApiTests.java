package marxo.test.local;

import com.google.common.net.MediaType;
import marxo.entity.node.Action;
import marxo.entity.node.Event;
import marxo.entity.node.Node;
import marxo.entity.node.PostFacebook;
import marxo.test.ApiTestConfiguration;
import marxo.test.BasicApiTests;
import marxo.test.Tester;
import org.joda.time.DateTime;
import org.joda.time.Seconds;
import org.springframework.data.mongodb.core.query.Criteria;
import org.testng.Assert;
import org.testng.annotations.Test;

@ApiTestConfiguration("http://localhost:8080/marxo/api/events/")
public class EventApiTests extends BasicApiTests {
	Event reusedEvent;
	Action reusedAction;
	Node reusedNode;

	@Test
	public void testCreateEvent() throws Exception {
		Criteria criteria = Criteria
				.where("tenantId").is(user.tenantId)
				.and("actions").not().size(0);

		Node node = new Node();
		node.setName("testCreateContent");
		reusedNode = node;

		PostFacebook postFacebook = new PostFacebook();
		node.getActions().add(postFacebook);
		reusedAction = postFacebook;

		mongoTemplate.insert(node);
		nodesToBeRemoved.add(node);

		reusedEvent = new Event();
		reusedEvent.setStartTime(DateTime.now().plus(Seconds.seconds(5)));
		reusedEvent.setAction(postFacebook);

		try (Tester tester = new Tester().basicAuth(email, password)) {
			tester
					.httpPost(reusedEvent)
					.send();
			tester
					.isCreated()
					.matchContentType(MediaType.JSON_UTF_8);

			Event event = tester.getContent(Event.class);
			Assert.assertEquals(event.id, reusedEvent.id);
			Assert.assertEquals(event.actionId, reusedEvent.id);
		}

		Node node1 = mongoTemplate.findById(reusedNode.id, Node.class);
		Assert.assertEquals(node1.getActions().get(0).getEvent().id, reusedNode.getActions().get(0).getEvent().id);
	}

	@Test(dependsOnMethods = "testCreateEvent")
	public void testReadEvent() throws Exception {
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

	@Test(dependsOnMethods = "testReadEvent")
	public void testUpdateEvent() throws Exception {
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

	@Test(dependsOnMethods = "testUpdateEvent")
	public void testDeleteEvent() throws Exception {
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
