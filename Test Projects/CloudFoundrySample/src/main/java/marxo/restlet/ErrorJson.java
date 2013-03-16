package marxo.restlet;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.jmkgreen.morphia.annotations.Embedded;

import javax.ws.rs.core.Response;
import java.util.HashMap;

public class ErrorJson {

	@JsonIgnore
	public final static HashMap<ErrorType, ErrorValueContainer> errorMap = new HashMap<ErrorType, ErrorValueContainer>() {{
		put(ErrorType.UNKNOWN, new ErrorValueContainer() {{
			status = Response.Status.INTERNAL_SERVER_ERROR;
			message = "Call 911!";
		}});
		put(ErrorType.ENTITY_NOT_FOUND, new ErrorValueContainer() {{
			status = Response.Status.NOT_FOUND;
			message = "The requested entity is not found.";
		}});
		put(ErrorType.INVALID_REQUEST, new ErrorValueContainer() {{
			status = Response.Status.BAD_REQUEST;
		}});
		put(ErrorType.ID_NOT_PROPERLY_FORMATTED, new ErrorValueContainer() {{
			status = Response.Status.BAD_REQUEST;
			message = "ID is not properly formatted.";
		}});
	}};

	public Error getError() {
		return error;
	}

	Error error;

	public ErrorJson(ErrorType type, String message) {
		this.error = new Error(type, message);
	}

	public static Response getResponse(ErrorType errorType, String message) {
		ErrorValueContainer errorValueContainer = errorMap.get(errorType);
		ErrorJson errorJson = new ErrorJson(errorType, (message == null) ? errorValueContainer.message : message);
		return Response.status(errorValueContainer.status).entity(errorJson).build();
	}

	@Embedded
	public class Error {
		@JsonProperty("type")
		ErrorType type = ErrorType.UNKNOWN;
		@JsonProperty("description")
		String description;

		Error(ErrorType type, String description) {
			this.type = type;
			this.description = description;
		}
	}

}

class ErrorValueContainer {
	Response.Status status;
	String message;
}
