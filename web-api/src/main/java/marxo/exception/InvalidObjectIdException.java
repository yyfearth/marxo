package marxo.exception;

public class InvalidObjectIdException extends RuntimeException {
	public String id;
	public String message;

	public InvalidObjectIdException(String id) {
		this(id, String.format("The given ID (%s) is invalid.", id));
	}

	public InvalidObjectIdException(String id, String message) {
		this.id = id;
		this.message = message;
	}
}
