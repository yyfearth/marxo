package marxo.controller;

import marxo.dao.TenantChildDao;
import marxo.entity.TenantChildEntity;
import marxo.security.MarxoAuthentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class TenantChildController<E extends TenantChildEntity, Dao extends TenantChildDao<E>> extends EntityController<E, Dao> {
	@SuppressWarnings("unchecked")
	protected TenantChildController(TenantChildDao dao) {
		super((Dao) dao);
	}

	public void setupDao() {
		MarxoAuthentication marxoAuthentication = (MarxoAuthentication) SecurityContextHolder.getContext().getAuthentication();
		user = marxoAuthentication.getUser();
		dao.setTenantId(user.tenantId);
	}
}
