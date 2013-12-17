package marxo.config;

import com.google.common.collect.Lists;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import marxo.serialization.DurationReadConverter;
import marxo.serialization.DurationWriteConverter;
import marxo.serialization.PeriodReadConverter;
import marxo.serialization.PeriodWriteConverter;
import marxo.tool.Systems;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.CustomConversions;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import java.net.UnknownHostException;

@ComponentScan(basePackages = "marxo.serialization")
@Configuration
public class MongoConfiguration {
	static final boolean isDebug = System.getProperty("debug") != null;

	MongoDbFactory mongoDbFactory;
	MongoClient mongoClient;
	MongoTemplate mongoTemplate;
	GridFsTemplate gridFsTemplate;
	MappingMongoConverter mappingMongoConverter;

	@Autowired
	DurationReadConverter durationReadConverter;
	@Autowired
	DurationWriteConverter durationWriteConverter;
	@Autowired
	PeriodReadConverter periodReadConverter;
	@Autowired
	PeriodWriteConverter periodWriteConverter;

	@Bean
	public Mongo mongo() throws UnknownHostException {
		if (mongoClient == null) {
			if (Systems.isWindows()) {
				mongoClient = new MongoClient("localhost", 27017);
			} else {
				mongoClient = new MongoClient("masonwan.com", 27017);
			}
		}

		return mongoClient;
	}

	@Bean
	public MongoDbFactory mongoDbFactory() throws UnknownHostException {
		if (mongoDbFactory == null) {
			mongoDbFactory = new SimpleMongoDbFactory(mongo(), "marxo");
		}

		return mongoDbFactory;
	}

	@Bean
	public MongoTemplate mongoTemplate() throws UnknownHostException {
		if (mongoTemplate == null) {
			mongoTemplate = new MongoTemplate(mongoDbFactory(), mappingMongoConverter());
		}

		return mongoTemplate;
	}

	@Bean
	public GridFsTemplate gridFsTemplate() throws UnknownHostException {
		if (gridFsTemplate == null) {
			gridFsTemplate = new GridFsTemplate(mongoDbFactory(), mappingMongoConverter());
		}

		return gridFsTemplate;
	}

	@Bean
	public MappingMongoConverter mappingMongoConverter() {
		if (mappingMongoConverter == null) {
			mappingMongoConverter = new MappingMongoConverter(mongoDbFactory, new MongoMappingContext());
			mappingMongoConverter.setCustomConversions(new CustomConversions(Lists.newArrayList(
					durationReadConverter,
					durationWriteConverter,
					periodReadConverter,
					periodWriteConverter
			)));
		}

		return mappingMongoConverter;
	}
}
