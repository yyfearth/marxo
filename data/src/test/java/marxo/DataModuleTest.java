package marxo;

import marxo.dao.WorkflowDao;
import marxo.dev.AdvancedGenerator;
import marxo.entity.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.testng.annotations.Test;

import java.util.List;

public class DataModuleTest {
	@Test
	public void canGenerateSampleData() throws Exception {
		AdvancedGenerator.main(new String[0]);
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("mongo-configuration.xml");
		MongoTemplate mongoTemplate = applicationContext.getBean(MongoTemplate.class);
		assert mongoTemplate.getDb().getName().equals("Marxo");

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
	public void testAutowired() throws Exception {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("mongo-configuration.xml");
		MongoTemplate mongoTemplate = applicationContext.getBean(MongoTemplate.class);
		WorkflowDao workflowDao = new WorkflowDao();
		List<Workflow> workflows = workflowDao.findAll();
		assert workflowDao.count() != 0;
	}
}