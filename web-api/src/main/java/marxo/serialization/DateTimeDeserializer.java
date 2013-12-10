package marxo.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.joda.time.DateTime;

import java.io.IOException;

public class DateTimeDeserializer extends JsonDeserializer<DateTime> {
	@Override
	public DateTime deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		String text = jp.getText();
		return DateTime.parse(text);
	}
}
