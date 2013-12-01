package marxo.entity.user;

import org.joda.time.DateTime;

public class Notification extends TenantChildEntity {
	public DateTime expireTime;
	public boolean isDisabled = false;

	public boolean isExpired() {
		return expireTime.isAfterNow();
	}
}
