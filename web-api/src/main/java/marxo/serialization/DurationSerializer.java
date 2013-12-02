package marxo.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.joda.time.Duration;

import java.io.IOException;

public class DurationSerializer extends JsonSerializer<Duration> {
	@Override
	public void serialize(Duration value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
		jgen.writeString(String.valueOf(value.getMillis()));
	}
}
