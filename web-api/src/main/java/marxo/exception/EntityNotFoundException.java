package marxo.exception;

import marxo.entity.BasicEntity;

public class EntityNotFoundException extends EntityException {
	protected String message;

	public EntityNotFoundException(String entityName, Object identity) {
		this(String.format("Cannot find [%s] with identity [%s]", entityName, identity.toString()));
	}

	public EntityNotFoundException(Class<? extends BasicEntity> aClass, Object identity) {
		this(aClass.getSimpleName(), identity);
	}

	public EntityNotFoundException(Object identity) {
		this("Entity", identity);
	}

	protected EntityNotFoundException(String message) {
		super(message);
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
