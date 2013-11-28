package marxo.exception;

public class DataInconsistentException extends DatabaseException {
	public DataInconsistentException() {
	}

	public DataInconsistentException(String message) {
		super(message);
	}
}
