package marxo.controller;

import marxo.entity.FacebookData;
import marxo.entity.FacebookStatus;
import marxo.entity.MongoDbAware;
import marxo.entity.user.Tenant;
import marxo.entity.user.User;
import marxo.exception.EntityNotFoundException;
import marxo.security.MarxoAuthentication;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/service{:s?}/facebook")
public class FacebookController implements MongoDbAware, InterceptorPreHandlable {
	Criteria criteria;
	User user;

	@Override
	public void preHandle() {
		MarxoAuthentication marxoAuthentication = (MarxoAuthentication) SecurityContextHolder.getContext().getAuthentication();
		Assert.notNull(marxoAuthentication);
		user = marxoAuthentication.getUser();
		criteria = Criteria.where("_id").is(user.tenantId);
	}

	@RequestMapping(method = RequestMethod.PUT)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public FacebookData saveData(@Valid @RequestBody FacebookData facebookData) {
		Assert.notNull(facebookData);

		facebookData.status = FacebookStatus.CONNECTED;

		Update update = Update.update("facebookData", facebookData);
		mongoTemplate.updateFirst(Query.query(criteria), update, Tenant.class);

		return facebookData;
	}

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public Object readData() {
		Tenant tenant = mongoTemplate.findOne(Query.query(criteria), Tenant.class);
		if (tenant == null) {
			throw new EntityNotFoundException(Tenant.class, user.tenantId);
		}

		return (tenant.facebookData == null) ? new FacebookData() : tenant.facebookData;
	}

	@RequestMapping(method = RequestMethod.DELETE)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public FacebookData deleteData() {
		Update update = new Update().unset("facebookData");
		Tenant tenant = mongoTemplate.findAndModify(Query.query(criteria), update, Tenant.class);
		return tenant.facebookData;
	}
}
