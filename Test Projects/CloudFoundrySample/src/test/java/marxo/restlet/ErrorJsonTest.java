package marxo.restlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import marxo.data.JsonParser;
import org.testng.annotations.Test;

public class ErrorJsonTest {

	@Test
	public void testAllTypes() throws JsonProcessingException {
		ErrorType[] errorTypes = ErrorType.values();

		for (ErrorType errorType : errorTypes) {
			ErrorJson errorJson = new ErrorJson(errorType, null);

			String json = JsonParser.getMapper().writeValueAsString(errorJson);
			System.out.println(json);
		}
	}
}
