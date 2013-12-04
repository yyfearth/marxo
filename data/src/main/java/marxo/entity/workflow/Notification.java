package marxo.entity.workflow;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.DateTime;

public class Notification extends WorkflowChildEntity {
	@JsonProperty("expires_at")
	public DateTime expireTime;
	public Type type = Type.DEFAULT;

	public boolean isDisabled = false;

	public boolean isExpired() {
		return expireTime.isAfterNow();
	}

	public static enum Type {
		DEFAULT,
		ROUTINE,
		REQUISITE,
		EMERGENT,
	}
}