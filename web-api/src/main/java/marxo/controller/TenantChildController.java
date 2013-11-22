package marxo.controller;

import marxo.dao.BasicDao;
import marxo.entity.user.TenantChildEntity;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

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
		daoContext = daoContext.addContext("tenantId", user.tenantId);
	}

	@Override
	public Entity create(@Valid @RequestBody Entity entity, HttpServletResponse response) throws Exception {
		entity.tenantId = user.tenantId;
		return super.create(entity, response);
	}
}
