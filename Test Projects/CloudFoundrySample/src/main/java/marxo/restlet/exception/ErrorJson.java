package marxo.restlet.exception;

import com.github.jmkgreen.morphia.annotations.Embedded;

public class ErrorJson {

	public Error getError() {
		return error;
	}

	Error error;

	public ErrorJson(String type, String message) {
		this.error = new Error(type, message);
	}

	@Embedded
	public class Error {
		public String getType() {
			return type;
		}

		public String getMessage() {
			return message;
		}

		String type;
		String message;

		Error(String type, String message) {
			this.type = type;
			this.message = message;
		}
	}

}