package marxo.exception;

import org.bson.types.ObjectId;

public class EntityNotFoundException extends EntityException {
	public EntityNotFoundException(ObjectId id) {
		this(id, String.format("The entity of the given ID (%s) does not exist", id));
	}

	protected EntityNotFoundException(ObjectId id, String message) {
		super(id, message);
	}
}
