package marxo.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.joda.time.DateTime;

import java.io.IOException;
import java.util.Date;

public class DateSerializer extends JsonSerializer<Date> {
	@Override
	public void serialize(Date value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
		DateTime dateTime = new DateTime(value);
		jgen.writeString(dateTime.toString());
	}
}
