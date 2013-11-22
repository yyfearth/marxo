package marxo.entity.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import marxo.dao.TenantDao;
import marxo.entity.BasicEntity;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Transient;

public class TenantChildEntity extends BasicEntity {
	public ObjectId tenantId;
	@Transient
	protected Tenant tenant;
	@Transient
	@Autowired
	protected TenantDao tenantDao;

	@JsonIgnore
	public Tenant getTenant() {
		return tenant;
	}

	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
	}
}
