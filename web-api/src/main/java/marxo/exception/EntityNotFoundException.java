package marxo.exception;

import org.bson.types.ObjectId;

public class EntityNotFoundException extends RuntimeException {
	public ObjectId objectId;

	public EntityNotFoundException(ObjectId objectId) {
		this.objectId = objectId;
	}
}
