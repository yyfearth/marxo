package marxo.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Period;

import java.util.Date;

public class MarxoObjectMapper extends ObjectMapper {
	public MarxoObjectMapper() {
		super();
		SimpleModule simpleModule = new SimpleModule();
		simpleModule.addSerializer(ObjectId.class, new ObjectIdSerializer());
		simpleModule.addDeserializer(ObjectId.class, new ObjectIdDeserializer());
		simpleModule.addSerializer(Date.class, new DateSerializer());
		simpleModule.addSerializer(DateTime.class, new DateTimeSerializer());
		simpleModule.addDeserializer(DateTime.class, new DateTimeDeserializer());
		simpleModule.addSerializer(Duration.class, new DurationSerializer());
		simpleModule.addDeserializer(Duration.class, new DurationDeserializer());
		simpleModule.addSerializer(Period.class, new PeriodSerializer());
		simpleModule.addDeserializer(Period.class, new PeriodDeserializer());
		registerModule(simpleModule);

		this.setPropertyNamingStrategy(new MarxoPropertyNamingStrategy());
	}
}
