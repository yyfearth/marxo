package marxo.controller;

import marxo.dao.TenantChildDao;
import marxo.entity.TenantChildEntity;
import marxo.security.MarxoAuthentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;

public class TenantChildController<E extends TenantChildEntity> extends EntityController<E> implements InterceptorPreHandlable {
	TenantChildDao<E> tenantChildDao;

	protected TenantChildController(TenantChildDao<E> tenantChildDao) {
		super(tenantChildDao);
		this.tenantChildDao = tenantChildDao;
	}

	public void preHandle() {
		MarxoAuthentication marxoAuthentication = (MarxoAuthentication) SecurityContextHolder.getContext().getAuthentication();
		Assert.notNull(marxoAuthentication);
		user = marxoAuthentication.getUser();
		tenantChildDao.setTenantId(user.tenantId);
	}
}
