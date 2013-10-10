package marxo.exception;

import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class EntityException extends RuntimeException {
	public ObjectId id;
	public List<String> messages;

	public EntityException(ObjectId id) {
		this.id = id;
	}

	protected EntityException(ObjectId id, String message) {
		this.id = id;
		this.messages = new ArrayList<>(Arrays.asList(message));
	}

	protected EntityException(ObjectId id, List<String> messages) {
		this.id = id;
		this.messages = messages;
	}
}
