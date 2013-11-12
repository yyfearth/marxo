package marxo.controller;

import marxo.dao.TenantChildDao;
import marxo.entity.TenantChildEntity;
import marxo.security.MarxoAuthentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

public class TenantChildController<E extends TenantChildEntity> extends EntityController<E> implements IInterceptroPreHandlable {
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

//	@Override
//	public E create(@Valid @RequestBody E entity, HttpServletResponse response) throws Exception {
//		entity.createdByUserId = entity.modifiedByUserId = user.id;
//		return super.create(entity, response);
//	}
//
//	@Override
//	public E update(@Valid @PathVariable String idString, @Valid @RequestBody E entity) {
//		entity.modifiedByUserId = user.id;
//		return super.update(idString, entity);
//	}
}
