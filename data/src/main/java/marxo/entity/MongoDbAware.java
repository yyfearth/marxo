package marxo.entity;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;

public interface MongoDbAware {
	static final ApplicationContext applicationContext = new ClassPathXmlApplicationContext("mongo-configuration.xml");
	static final MongoTemplate mongoTemplate = applicationContext.getBean(MongoTemplate.class);
}
