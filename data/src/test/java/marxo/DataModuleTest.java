package marxo;

import marxo.dev.AdvancedGenerator;
import marxo.entity.link.Link;
import marxo.entity.node.Node;
import marxo.entity.user.Tenant;
import marxo.entity.user.User;
import marxo.entity.workflow.Workflow;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

@SuppressWarnings("uncheck")
public class DataModuleTest {
	ApplicationContext applicationContext = new ClassPathXmlApplicationContext("mongo-configuration.xml");
	MongoTemplate mongoTemplate = applicationContext.getBean(MongoTemplate.class);

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
			List<Class> list = mongoTemplate.findAll(aClass);
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
	public void testJodaTime() throws Exception {
		DateTime dateTime1 = new DateTime("2013-11-09");
		DateTime dateTime2 = new DateTime("2013-11-10");
		Duration duration = new Duration(dateTime1.getMillis(), dateTime2.getMillis());
		System.out.println(duration);
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
}