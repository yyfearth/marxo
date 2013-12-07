package marxo.exception;

import marxo.entity.BasicEntity;

import java.util.List;

public class EntityNotFoundException extends EntityException {
	protected String message;

	public EntityNotFoundException(String message, String message1) {
		super(message);
		message = message1;
	}

	public EntityNotFoundException(List<String> messages, String message) {
		super(messages);
		this.message = message;
	}

	public EntityNotFoundException(Class<? extends BasicEntity> aClass, Object identity) {
		this(String.format("The %s of the given ID (%s) does not exist", aClass.getSimpleName(), identity.toString()));
	}

	protected EntityNotFoundException(String message) {
		super(message);
		this.message = message;
	}
}
