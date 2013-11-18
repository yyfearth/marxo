package marxo.controller;

import marxo.dao.TenantDao;
import marxo.entity.FacebookData;
import marxo.entity.FacebookStatus;
import marxo.entity.user.Tenant;
import marxo.entity.user.User;
import marxo.exception.EntityNotFoundException;
import marxo.security.MarxoAuthentication;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/service{:s?}/facebook")
public class FacebookController implements InterceptorPreHandlable {
	User user;
	TenantDao tenantDao;

	@Override
	public void preHandle() {
		MarxoAuthentication marxoAuthentication = (MarxoAuthentication) SecurityContextHolder.getContext().getAuthentication();
		Assert.notNull(marxoAuthentication);
		user = marxoAuthentication.getUser();
		tenantDao = new TenantDao();
	}

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public FacebookData readData() {
		Tenant tenant = tenantDao.findOne(user.tenantId);
		if (tenant == null) {
			throw new EntityNotFoundException(user.tenantId);
		}

		return (tenant.facebookData == null) ? new FacebookData() : tenant.facebookData;
	}

	@RequestMapping(method = RequestMethod.PUT)
	@ResponseBody
	public FacebookData saveData(@Valid @RequestBody FacebookData facebookData) {
		Assert.notNull(facebookData);
		Tenant tenant = tenantDao.findOne(user.tenantId);

		// todo: call channel
		facebookData.status = FacebookStatus.CONNECTED;

		tenantDao.updateFacebookData(tenant.id, facebookData);

		return facebookData;
	}

	@RequestMapping(method = RequestMethod.DELETE)
	@ResponseBody
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteData() {
		tenantDao.removeFacebookData(user.tenantId);
	}
}
