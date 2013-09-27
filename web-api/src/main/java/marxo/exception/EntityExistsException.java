package marxo.exception;

import org.bson.types.ObjectId;

public class EntityExistsException extends EntityException {
	public EntityExistsException(ObjectId id) {
		this(id, String.format("The given ID (%s) already exists", id));
	}

	public EntityExistsException(ObjectId id, String message) {
		super(id, message);
	}
}
