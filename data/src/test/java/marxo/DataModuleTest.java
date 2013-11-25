package marxo;

import com.google.common.collect.Collections2;
import com.mongodb.DB;
import marxo.dev.AdvancedGenerator;
import marxo.entity.BasicEntity;
import marxo.entity.link.Link;
import marxo.entity.node.Node;
import marxo.entity.user.Tenant;
import marxo.entity.user.User;
import marxo.entity.user.UserType;
import marxo.entity.workflow.Workflow;
import marxo.tool.Loggable;
import marxo.tool.PasswordEncryptor;
import marxo.validation.SelectIdFunction;
import org.bson.types.ObjectId;
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

@SuppressWarnings("uncheck")
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
		Criteria criteria = Criteria.where("id").in(Collections2.transform(entitiesToRemove, SelectIdFunction.getInstance()));
		for (String collectionName : mongoTemplate.getCollectionNames()) {
			mongoTemplate.remove(Query.query(criteria), collectionName);
		}
	}

	@Test
	public void canGenerateSampleData() throws Exception {
		AdvancedGenerator.main(new String[0]);
		assert mongoTemplate.getDb().getName().toLowerCase().equals("marxo");

		Class[] classes = new Class[]{
				Tenant.class,
				User.class,
				Workflow.class,
				Node.class,
				Link.class,
		};

		for (Class aClass : classes) {
			List list = mongoTemplate.findAll(aClass);
			long count = list.size();
			String message = "Collection " + aClass + " has only " + count + " record(s)";
			assert count >= 2 : message;
			System.out.println(message);
		}
	}

	@Test
	public void testPrefixCriteria() throws Exception {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath*:mongo-configuration.xml");
		MongoTemplate mongoTemplate = (MongoTemplate) applicationContext.getBean("mongoTemplate");
		List<Workflow> workflows = mongoTemplate.find(Query.query(new Criteria()), Workflow.class);
		System.out.println(workflows);
		assert workflows != null;
	}

	@Test
	public void testChainedQuery() throws Exception {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("mongo-configuration.xml");
		MongoTemplate mongoTemplate = applicationContext.getBean(MongoTemplate.class);
		Criteria criteria = Criteria.where("name").regex(".*").and("isProject").is(true).and("id").regex("\\d");
		List<Workflow> workflows = mongoTemplate.find(Query.query(criteria), Workflow.class);
	}

	@Test
	public void testOrQuery() throws Exception {
		ObjectId tenantId = new ObjectId("528b805bcf0fed1c40ed34f5");
		List<Workflow> workflows;

		Criteria criteria1 = Criteria.where("tenantId").is(tenantId);
		workflows = mongoTemplate.find(Query.query(criteria1), Workflow.class);
		int size1 = workflows.size();

		Criteria criteria2 = Criteria.where("tenantId").exists(false);
		workflows = mongoTemplate.find(Query.query(criteria2), Workflow.class);
		int size2 = workflows.size();

		Criteria criteria = new Criteria().orOperator(criteria1, criteria2);
		workflows = mongoTemplate.find(Query.query(criteria), Workflow.class);
		Assert.assertEquals(workflows.size(), size1 + size2);
		for (Workflow workflow : workflows) {
			if (workflow.tenantId != null) {
				Assert.assertEquals(workflow.tenantId, tenantId);
			}
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
}