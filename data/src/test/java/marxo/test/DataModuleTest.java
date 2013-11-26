package marxo.test;

import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.mongodb.DB;
import marxo.entity.BasicEntity;
import marxo.entity.FacebookData;
import marxo.entity.link.Link;
import marxo.entity.node.Action;
import marxo.entity.node.Node;
import marxo.entity.node.PostFacebook;
import marxo.entity.user.Tenant;
import marxo.entity.user.User;
import marxo.entity.user.UserType;
import marxo.entity.workflow.Workflow;
import marxo.tool.Loggable;
import marxo.tool.PasswordEncryptor;
import marxo.validation.SelectIdFunction;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.testng.Assert;
import org.testng.annotations.*;

import javax.crypto.SecretKeyFactory;
import javax.xml.bind.DatatypeConverter;
import java.util.*;

@SuppressWarnings({"uncheck", "unchecked"})
public class DataModuleTest implements Loggable {
	ApplicationContext applicationContext = new ClassPathXmlApplicationContext("mongo-configuration.xml");
	MongoTemplate mongoTemplate = applicationContext.getBean(MongoTemplate.class);
	Set<BasicEntity> entitiesToRemove = new HashSet<>();

	@BeforeMethod
	public void beforeMethod() throws Exception {

	}

	@AfterMethod
	public void afterMethod() throws Exception {

	}

	@BeforeClass
	public void beforeClass() throws Exception {
	}

	@AfterClass
	public void afterClass() throws Exception {
		Criteria criteria = Criteria.where("_id").in(Collections2.transform(entitiesToRemove, SelectIdFunction.getInstance()));
		for (String collectionName : mongoTemplate.getCollectionNames()) {
			mongoTemplate.remove(Query.query(criteria), collectionName);
		}
	}

	@Test(priority = 10000)
	public void cleanDatabase() throws Exception {
		DB db = mongoTemplate.getDb();
		logger.info(String.format("Using database [%s]", db.getName()));

		Set<String> databaseNames = db.getCollectionNames();
		for (String databaseName : databaseNames) {
			if (!databaseName.startsWith("system")) {
				logger.info(String.format("Drop collection [%s]", databaseName));
				mongoTemplate.dropCollection(databaseName);
			}
		}
	}

	@Test(dependsOnMethods = {"cleanDatabase"})
	public void addTenant() throws Exception {
		Tenant tenant = new Tenant();
		tenant.setName("Marxo");
		tenant.description = "A tall, a good guy, and a cat.";
		tenant.phoneNumber = "(408) 888-8888";
		tenant.email = "marxo@gmail.com";

		FacebookData facebookData = new FacebookData();
		facebookData.accessToken = "CAADCM9YpGYwBANeLvBD7aswljKFqsBYZAAUZC9ohrKoPkR0OQ8yZA1kMZAIwBuLFsxPnnRaUsuIjB40Q9i8qn2BNlaITfkKsQYE4LFatfAY6okQgYe4b8fYcr400YdQP98Wp4SFZBG6MOMCtC3pJNsZCVB3bBpXZCyKvbj66SwBWjBW1ZAAZBYT2a";
		facebookData.expireTime = DateTime.parse("2014-01-14T08:22:54.541Z");
		Assert.assertTrue(facebookData.updateToken());
		tenant.facebookData = facebookData;
		tenant.save();

		Assert.assertTrue(mongoTemplate.exists(Query.query(Criteria.where("name").is("Marxo")), Tenant.class));
	}

	@Test(dependsOnMethods = {"addTenant"})
	public void addUsers() throws Exception {
		ApplicationContext securityContext = new ClassPathXmlApplicationContext("classpath*:security.xml");
		byte[] salt = DatatypeConverter.parseHexBinary((String) securityContext.getBean("passwordSaltHexString"));
		SecretKeyFactory secretKeyFactory = (SecretKeyFactory) securityContext.getBean("secretKeyFactory");
		PasswordEncryptor passwordEncryptor = new PasswordEncryptor(salt, secretKeyFactory);

		Tenant tenant = mongoTemplate.findOne(Query.query(Criteria.where("name").is("Marxo")), Tenant.class);
		List<User> users = new ArrayList<>();
		{
			User user;

			user = new User();
			user.setTenant(tenant);
			user.setName("Tester");
			user.setEmail("test@example.com");
			user.setPassword(passwordEncryptor.encrypt("test"));
			users.add(user);

			user = new User();
			user.setTenant(tenant);
			user.setName("Wilson");
			user.setEmail("yyfearth@gmail.com");
			user.setPassword(passwordEncryptor.encrypt("2k96H29ECsJ05BJAkEGm6FC+UgjwVTc1qOd7SGG2uS8"));
			users.add(user);

			user = new User();
			user.setTenant(tenant);
			user.setName("Leo");
			user.setEmail("otaru14204@hotmail.com");
			user.setPassword(passwordEncryptor.encrypt("XELXdnuv/p7QeCzPM7Pl7TLfd6o2NZSaPb/sGtYUg5Q"));
			users.add(user);
		}

		for (User user : users) {
			user.type = UserType.PUBLISHER;
		}

		mongoTemplate.insert(users, User.class);

		Assert.assertEquals(mongoTemplate.count(new Query(), User.class), 3);
	}

	@Test
	public void nodeWire() throws Exception {
		List<BasicEntity> entitiesToSave = new ArrayList<>();

		Tenant tenant = new Tenant();
		entitiesToSave.add(tenant);

		Node node = new Node();
		node.setTenant(tenant);
		entitiesToSave.add(node);

		Action action1 = new PostFacebook();
		node.getActions().add(action1);

		Action action2 = new PostFacebook();
		node.getActions().add(action2);

		Action action3 = new PostFacebook();
		node.getActions().add(action3);

		node.wire();
		mongoTemplate.insertAll(entitiesToSave);
		entitiesToRemove.addAll(entitiesToSave);

		node = Node.get(node.id);

		Assert.assertNotNull(node.getFirstAction());
		Assert.assertEquals(node.getFirstAction().id, action1.id);

		Assert.assertEquals(node.getFirstAction().getNextAction().id, action2.id);
		Assert.assertEquals(node.getFirstAction().getNextAction().getNextAction().id, action3.id);
		Assert.assertNull(node.getFirstAction().getNextAction().getNextAction().getNextAction());

		Map<ObjectId, Action> actionMap = node.getActionMap();
		Assert.assertNotNull(actionMap);
		Assert.assertEquals(actionMap.size(), 3);

		for (Action action : node.getActions()) {
			Assert.assertEquals(action.tenantId, tenant.id);
		}
	}

	@Test
	public void wireWorkflow() throws Exception {
		List<BasicEntity> entitiesToSave = new ArrayList<>();

		Tenant tenant = new Tenant();
		entitiesToSave.add(tenant);

		Workflow workflow = new Workflow();
		workflow.setTenant(tenant);
		entitiesToSave.add(workflow);

		Node node1 = new Node();
		Node node2 = new Node();
		Node node3 = new Node();

		workflow.setNodes(Lists.newArrayList(node1, node2, node3));
		entitiesToSave.addAll(Lists.newArrayList(node1, node2, node3));

		Link link1 = new Link();
		link1.previousNodeId = node1.id;
		link1.nextNodeId = node2.id;

		Link link2 = new Link();
		link2.previousNodeId = node2.id;
		link2.nextNodeId = node3.id;

		workflow.setLinks(Lists.newArrayList(link1, link2));
		entitiesToSave.addAll(Lists.newArrayList(link1, link2));

		mongoTemplate.insertAll(entitiesToSave);
		entitiesToRemove.addAll(entitiesToSave);
		workflow.deepWire();

		workflow = Workflow.get(workflow.id);
		Assert.assertEquals(workflow.getStartNode().id, node1.id);

		node1 = Node.get(node1.id);
		node2 = Node.get(node2.id);
		node3 = Node.get(node3.id);
		link1 = Link.get(link1.id);
		link2 = Link.get(link2.id);

		Assert.assertTrue(node1.getFromLinkIds().isEmpty());
		Assert.assertEquals(node1.getToLinkIds(), Arrays.asList(link1.id));
		Assert.assertEquals(node1.getToNodeIds(), Arrays.asList(node2.id));

		Assert.assertEquals(node2.getFromLinkIds(), Arrays.asList(link1.id));
		Assert.assertEquals(node2.getToLinkIds(), Arrays.asList(link2.id));
		Assert.assertEquals(node2.getToNodeIds(), Arrays.asList(node3.id));

		Assert.assertEquals(node3.getFromLinkIds(), Arrays.asList(link2.id));
		Assert.assertTrue(node3.getToLinkIds().isEmpty());
		Assert.assertTrue(node3.getToNodeIds().isEmpty());

		for (Node node : Lists.newArrayList(node1, node2, node3)) {
			Assert.assertEquals(node.tenantId, tenant.id);
		}

		for (Link link : Lists.newArrayList(link1, link2)) {
			Assert.assertEquals(link.tenantId, tenant.id);
		}
	}
}