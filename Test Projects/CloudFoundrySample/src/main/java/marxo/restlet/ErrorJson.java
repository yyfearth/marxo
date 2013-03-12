package marxo.restlet;

import com.github.jmkgreen.morphia.annotations.Embedded;

import java.util.HashMap;

public class ErrorJson {
	public Error getError() {
		return error;
	}

	public void setError(Error error) {
		this.error = error;
	}

	Error error;

	public ErrorJson(ErrorType type) {
		this.error = new Error(type);
	}
}

@Embedded
class Error {
	public String getDescription() {
		return description;
	}

	protected void setDescription(String description) {
		this.description = description;
	}

	public ErrorType getType() {
		return type;
	}

	protected void setType(ErrorType type) {
		this.type = type;
		description = map.get(type);
	}

	ErrorType type = ErrorType.Unknown;
	String description;

	HashMap<ErrorType, String> map = new HashMap<ErrorType, String>() {{
		put(ErrorType.Unknown, "Unknown error. Please call 911.");
		put(ErrorType.EntityNotFound, "Requested entity is not found.");
		put(ErrorType.InvalidRequest, "Request is not valid.");
	}};

	Error(ErrorType type) {
		setType(type);
	}
}
