package marxo.exception;

import marxo.entity.BasicEntity;

public class EntityNotFoundException extends EntityException {
	protected String message;

	public EntityNotFoundException(Class<? extends BasicEntity> aClass, Object identity) {
		this(String.format("The %s of the given ID (%s) does not exist", aClass.getSimpleName(), identity.toString()));
	}

	protected EntityNotFoundException(String message) {
		super(message);
		this.message = message;
	}
}
