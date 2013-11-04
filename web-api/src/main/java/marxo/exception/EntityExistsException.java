package marxo.exception;

import org.bson.types.ObjectId;

public class EntityExistsException extends EntityException {
	ObjectId objectId;

	public EntityExistsException(ObjectId id) {
		this(id, String.format("The given ID (%s) already exists", id));
	}

	public EntityExistsException(ObjectId id, String message) {
		super(message);
		this.objectId = id;
	}
}
