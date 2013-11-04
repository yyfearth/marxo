package marxo.exception;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class EntityException extends RuntimeException {
	public List<String> messages;

	protected EntityException(String message) {
		this.messages = new ArrayList<>(Arrays.asList(message));
	}

	protected EntityException(List<String> messages) {
		this.messages = messages;
	}
}
