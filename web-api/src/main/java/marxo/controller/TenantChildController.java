package marxo.controller;

import marxo.entity.user.TenantChildEntity;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

public abstract class TenantChildController<Entity extends TenantChildEntity> extends EntityController<Entity> {
	/**
	 * The user object will be set before each controller method is called.
	 */
	@Override
	public void preHandle() {
		super.preHandle();
		Criteria criteria1 = Criteria.where("tenantId").is(user.tenantId);
		Criteria criteria2 = Criteria.where("tenantId").exists(false);
		criteria.orOperator(criteria1, criteria2);
	}

	@Override
	public Entity create(@Valid @RequestBody Entity entity, HttpServletResponse response) throws Exception {
		entity.tenantId = user.tenantId;
		return super.create(entity, response);
	}
}