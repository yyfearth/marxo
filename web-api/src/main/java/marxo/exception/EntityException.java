package marxo.exception;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

public abstract class EntityException extends RuntimeException {
	public List<String> messages;

	protected EntityException(String message) {
		this.messages = new ArrayList<>(Lists.newArrayList(message));
	}

	protected EntityException(List<String> messages) {
		this.messages = messages;
	}
}
