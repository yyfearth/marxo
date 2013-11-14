package marxo.dao;

import marxo.entity.Notification;
import org.bson.types.ObjectId;

public class NotificationDao extends TenantChildDao<Notification> {
	public NotificationDao(ObjectId tenantId) {
		super(tenantId);
	}
}
