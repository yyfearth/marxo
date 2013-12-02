package marxo.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.joda.time.Period;

import java.io.IOException;

public class PeriodSerializer extends JsonSerializer<Period> {
	@Override
	public void serialize(Period value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
		jgen.writeString(String.valueOf(value.getMillis()));
	}
}
