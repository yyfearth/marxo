package marxo.exception;

public class EntityExistsException extends RuntimeException {
	public String message;

	public EntityExistsException(Object identity) {
		message = String.format("The given ID (%s) already exists", identity);
	}
}
