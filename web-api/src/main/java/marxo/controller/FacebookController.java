package marxo.controller;

import marxo.dao.TenantDao;
import marxo.entity.FacebookData;
import marxo.entity.FacebookStatus;
import marxo.entity.Tenant;
import marxo.entity.User;
import marxo.exception.EntityNotFoundException;
import marxo.security.MarxoAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/service{:s?}/facebook")
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

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public FacebookData readData() {
		Tenant tenant = tenantDao.get(user.tenantId);
		if (tenant == null) {
			throw new EntityNotFoundException(user.tenantId);
		}

		return (tenant.facebookData == null) ? new FacebookData() : tenant.facebookData;
	}

	@RequestMapping(method = RequestMethod.PUT)
	@ResponseBody
	public FacebookData saveData(@Valid @RequestBody FacebookData facebookData) {
		Assert.notNull(facebookData);
		Tenant tenant = tenantDao.get(user.tenantId);

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
