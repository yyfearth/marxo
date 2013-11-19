package marxo.controller;

import marxo.dao.BasicDao;
import marxo.entity.user.TenantChildEntity;
import marxo.security.MarxoAuthentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;

public abstract class TenantChildController<Entity extends TenantChildEntity> extends EntityController<Entity> {
	protected TenantChildController(BasicDao<Entity> dao) {
		super(dao);
	}

	/**
	 * The user object will be set before each controller method is called.
	 */
	@Override
	public void preHandle() {
		super.preHandle();
		daoContext=daoContext.addContext("tenantId", user.tenantId);
	}
}
