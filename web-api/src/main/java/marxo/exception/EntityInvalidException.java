package marxo.exception;

import org.bson.types.ObjectId;

import java.util.List;

public class EntityInvalidException extends EntityException {
	public EntityInvalidException(ObjectId id) {
		this(id, String.format("The entity with ID (%s) is invalid.", id));
	}

	public EntityInvalidException(ObjectId id, String message) {
		super(message);
	}

	public EntityInvalidException(ObjectId id, List<String> messages) {
		super(messages);
	}
}
