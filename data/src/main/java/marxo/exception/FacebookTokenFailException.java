package marxo.exception;

import org.bson.types.ObjectId;

public class FacebookTokenFailException extends RuntimeException {
	public final ObjectId tenantId;

	public FacebookTokenFailException(ObjectId tenantId) {
		this.tenantId = tenantId;
	}
}
