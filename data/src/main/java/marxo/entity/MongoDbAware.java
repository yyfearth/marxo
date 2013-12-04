package marxo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.MongoTemplate;

public interface MongoDbAware {
	@JsonIgnore
	@Transient
	public static final ApplicationContext applicationContext = new ClassPathXmlApplicationContext("mongo-configuration.xml");
	@JsonIgnore
	@Transient
	public static final MongoTemplate mongoTemplate = applicationContext.getBean(MongoTemplate.class);
}
