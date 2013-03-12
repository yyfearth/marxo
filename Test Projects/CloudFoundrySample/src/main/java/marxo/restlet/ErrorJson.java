package marxo.restlet;

import com.github.jmkgreen.morphia.annotations.Embedded;

import java.util.HashMap;

public class ErrorJson {
	Error error;

	public ErrorJson(ErrorType type) {
		this.error = new Error(type);
	}
}

@Embedded
class Error {
	ErrorType type = ErrorType.Unknown;
	String description = "";

	HashMap<ErrorType, String> map = new HashMap<ErrorType, String>() {{
		put(ErrorType.Unknown, "");
		put(ErrorType.EntityNotFound, "");
	}};

	Error(ErrorType type) {
		this.type = type;
	}
}
