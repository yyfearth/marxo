package marxo.controller;

import marxo.entity.FacebookData;
import marxo.entity.FacebookStatus;
import marxo.entity.user.Tenant;
import marxo.entity.user.User;
import marxo.exception.EntityNotFoundException;
import marxo.security.MarxoAuthentication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
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
public class FacebookController implements InterceptorPreHandlable {
	protected static final ApplicationContext applicationContext = new ClassPathXmlApplicationContext("mongo-configuration.xml");
	protected static final MongoTemplate mongoTemplate = applicationContext.getBean(MongoTemplate.class);
	Criteria criteria;
	User user;

	@Override
	public void preHandle() {
		MarxoAuthentication marxoAuthentication = (MarxoAuthentication) SecurityContextHolder.getContext().getAuthentication();
		Assert.notNull(marxoAuthentication);
		user = marxoAuthentication.getUser();
		criteria.and("tenantId").is(user.tenantId);
	}

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public Object readData() {
		Tenant tenant = mongoTemplate.findOne(Query.query(criteria), Tenant.class);
		if (tenant == null) {
			throw new EntityNotFoundException(Tenant.class, user.tenantId);
		}

		return (tenant.facebookData == null) ? new FacebookData() : tenant.facebookData;
	}

	@RequestMapping(method = RequestMethod.PUT)
	@ResponseBody
	public FacebookData saveData(@Valid @RequestBody FacebookData facebookData) {
		Assert.notNull(facebookData);
		Tenant tenant = mongoTemplate.findOne(Query.query(criteria), Tenant.class);

		// todo: call channel
		facebookData.status = FacebookStatus.CONNECTED;

		Update update = Update.update("facebookData", facebookData);
		mongoTemplate.updateFirst(Query.query(criteria), update, Tenant.class);

		return facebookData;
	}

	@RequestMapping(method = RequestMethod.DELETE)
	@ResponseBody
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteData() {
		Update update = new Update().unset("facebookData");
		mongoTemplate.updateFirst(Query.query(criteria), update, Tenant.class);
	}
}
