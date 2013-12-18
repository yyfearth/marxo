package marxo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import marxo.config.MongoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

public interface MongoDbAware {
	@JsonIgnore
	@Transient
	final static ApplicationContext applicationContext = new AnnotationConfigApplicationContext(MongoConfiguration.class);

	@JsonIgnore
	@Transient
	MongoTemplate mongoTemplate = applicationContext.getBean(MongoTemplate.class);
	@JsonIgnore
	@Transient
	GridFsTemplate gridFsTemplate = applicationContext.getBean(GridFsTemplate.class);
	@JsonIgnore
	@Transient
	MappingMongoConverter mappingConverter = applicationContext.getBean(MappingMongoConverter.class);
}
