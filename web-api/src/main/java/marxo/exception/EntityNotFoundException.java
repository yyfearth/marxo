package marxo.exception;

import org.bson.types.ObjectId;

public class EntityNotFoundException extends EntityException {
	protected String identity;
	protected String message;

	public EntityNotFoundException(ObjectId objectId) {
		this(objectId.toString());
	}

	public EntityNotFoundException(String identity) {
		this(identity, String.format("The entity of the given ID (%s) does not exist", identity));
	}

	protected EntityNotFoundException(String identity, String message) {
		super(message);
		this.identity = identity;
		this.message = message;
	}
}
