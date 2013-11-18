package marxo.entity.user;

import marxo.entity.BasicEntity;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Transient;

public class TenantChildEntity extends BasicEntity {
	public ObjectId tenantId;
	@Transient
	public Tenant tenant;
}
