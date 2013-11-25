package marxo.test;

import com.google.common.collect.Collections2;
import com.mongodb.DB;
import marxo.entity.BasicEntity;
import marxo.entity.FacebookData;
import marxo.entity.node.Action;
import marxo.entity.node.Node;
import marxo.entity.node.PostFacebook;
import marxo.entity.user.Tenant;
import marxo.entity.user.User;
import marxo.entity.user.UserType;
import marxo.tool.Loggable;
import marxo.tool.PasswordEncryptor;
import marxo.validation.NodeValidator;
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
	public void autoWireActionsInNode() throws Exception {
		List<BasicEntity> entitiesToSave = new ArrayList<>();

		Node node = new Node();
		entitiesToSave.add(node);


		Action action1 = new PostFacebook();
		entitiesToSave.add(action1);
		node.getActions().add(action1);

		Action action2 = new PostFacebook();
		entitiesToSave.add(action2);
		node.getActions().add(action2);

		Action action3 = new PostFacebook();
		entitiesToSave.add(action3);
		node.getActions().add(action3);

		mongoTemplate.insertAll(entitiesToSave);
		entitiesToRemove.addAll(entitiesToSave);

		node = Node.get(node.id);
		NodeValidator.wire(node);

		Assert.assertNotNull(node.firstAction());
		Assert.assertEquals(node.firstAction().id, action1.id);

		Assert.assertEquals(node.firstAction().getNextAction().id, action2.id);
		Assert.assertEquals(node.firstAction().getNextAction().getNextAction().id, action3.id);
		Assert.assertNull(node.firstAction().getNextAction().getNextAction().getNextAction());

		Map<ObjectId, Action> actionMap = node.getActionMap();
		Assert.assertNotNull(actionMap);
		Assert.assertEquals(actionMap.size(), 3);
	}
}