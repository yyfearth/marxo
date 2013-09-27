package marxo.exception;

import org.bson.types.ObjectId;

public abstract class EntityException extends RuntimeException {
	public ObjectId id;
	public String message;

	public EntityException(ObjectId id) {
		this.id = id;
	}

	protected EntityException(ObjectId id, String message) {
		this.id = id;
		this.message = message;
	}
}
