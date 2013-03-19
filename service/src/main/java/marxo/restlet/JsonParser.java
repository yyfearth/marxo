package marxo.restlet;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JsonParser {
	static ObjectMapper objectMapper;

	static {
		objectMapper = new ObjectMapper();

		// To increase performance.
		// MapperFeature (jackson-databind 2.0.2 API)
		// http://fasterxml.github.com/jackson-databind/javadoc/2.0.2/com/fasterxml/jackson/databind/MapperFeature.html#USE_STATIC_TYPING
		objectMapper.configure(MapperFeature.USE_STATIC_TYPING, true);
		objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		// Do not serialize the fields with null values.
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
	}

	public static ObjectMapper getMapper() {
		return objectMapper;
	}
}
