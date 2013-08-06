package marxo.restlet.exception;

public class ErrorJson {

	Error error;

	public ErrorJson(String type, String message) {
		this.error = new Error(type, message);
	}

	public Error getError() {
		return error;
	}

	public class Error {
		String type;
		String message;

		Error(String type, String message) {
			this.type = type;
			this.message = message;
		}

		public String getType() {
			return type;
		}

		public String getMessage() {
			return message;
		}
	}

}
