package marxo.controller;

import marxo.dao.TenantChildDao;
import marxo.entity.TenantChildEntity;
import marxo.security.MarxoAuthentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class RestrictedEntityController<E extends TenantChildEntity, Dao extends TenantChildDao<E>> extends EntityController<E, Dao> {
	protected RestrictedEntityController(TenantChildDao dao) {
		super((Dao) dao);
	}

	protected void setupDao() {
		MarxoAuthentication marxoAuthentication = (MarxoAuthentication) SecurityContextHolder.getContext().getAuthentication();
		user = marxoAuthentication.getUser();
		dao.setTenantId(user.tenantId);
	}
}
