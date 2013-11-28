package marxo.exception;

import marxo.entity.BasicEntity;

public class KeyNotFoundException extends RuntimeException {
	String key;
	BasicEntity entity;

	public KeyNotFoundException(String key, BasicEntity entity) {
		this.key = key;
		this.entity = entity;
	}
}
