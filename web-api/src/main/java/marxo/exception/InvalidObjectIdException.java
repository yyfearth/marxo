package marxo.exception;

public class InvalidObjectIdException extends RuntimeException {
	public String id;

	public InvalidObjectIdException(String id) {
		this.id = id;
	}
}
