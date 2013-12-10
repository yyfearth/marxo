package marxo.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.bson.types.ObjectId;
import org.springframework.util.Assert;

import java.io.IOException;

public class ObjectIdDeserializer extends JsonDeserializer<ObjectId> {
	@Override
	public ObjectId deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		String text = jp.getText();
		Assert.isTrue(ObjectId.isValid(text));
		return new ObjectId(text);
	}
}
