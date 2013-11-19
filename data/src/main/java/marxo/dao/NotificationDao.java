package marxo.dao;

import marxo.entity.Notification;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

@Repository
public class NotificationDao extends TenantChildDao<Notification> {
}
