package marxo.controller;

import marxo.dao.TenantDao;
import marxo.entity.FacebookData;
import marxo.entity.Tenant;
import marxo.entity.User;
import marxo.exception.EntityNotFoundException;
import marxo.security.MarxoAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

@Controller
@RequestMapping("/service/facebook")
public class FacebookController extends BasicController implements IInterceptroPreHandlable {
	User user;
	@Autowired
	TenantDao tenantDao;

	@Override
	public void preHandle() {
		MarxoAuthentication marxoAuthentication = (MarxoAuthentication) SecurityContextHolder.getContext().getAuthentication();
		Assert.notNull(marxoAuthentication);
		user = marxoAuthentication.getUser();
	}

	@RequestMapping(method = RequestMethod.PUT)
	@ResponseBody
	public FacebookData saveData(@Valid @RequestBody FacebookData facebookData) {
		Assert.notNull(facebookData);
		Tenant tenant = tenantDao.get(user.tenantId);
		tenantDao.updateFacebookData(tenant.id, facebookData);
		return facebookData;
	}

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public FacebookData readData() {
		Tenant tenant = tenantDao.get(user.tenantId);
		if (tenant == null) {
			throw new EntityNotFoundException(user.tenantId);
		}

		return (tenant.facebookData == null) ? new FacebookData() : tenant.facebookData;
	}
}
