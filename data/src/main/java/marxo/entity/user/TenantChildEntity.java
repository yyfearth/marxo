package marxo.entity.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import marxo.entity.BasicEntity;
import marxo.entity.workflow.RunStatus;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Transient;

public abstract class TenantChildEntity extends BasicEntity {
	public ObjectId tenantId;
	@Transient
	protected Tenant tenant;

	@Transient

	@JsonIgnore
	public Tenant getTenant() {
		if (tenantId == null) {
			return null;
		}
		return (tenant == null) ? (tenant = mongoTemplate.findById(this.tenantId, Tenant.class)) : tenant;
	}

	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
		tenantId = tenant.id;
	}
}
