package marxo.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.joda.time.Duration;

import java.io.IOException;

public class DurationDeserializer extends JsonDeserializer<Duration> {
	@Override
	public Duration deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		String text = jp.getText();
		return Duration.millis(Long.parseLong(text));
	}
}
