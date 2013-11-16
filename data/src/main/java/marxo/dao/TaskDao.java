package marxo.dao;

import marxo.entity.Task;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;

public class TaskDao extends BasicDao<Task> {
	static protected final MongoTemplate mongoTemplate = (MongoTemplate) new ClassPathXmlApplicationContext("classpath*:mongo-configuration.xml").getBean("mongoTemplate");
}
