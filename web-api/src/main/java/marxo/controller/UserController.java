package marxo.controller;

import marxo.entity.user.User;
import marxo.exception.EntityExistsException;
import marxo.exception.EntityNotFoundException;
import marxo.exception.InvalidObjectIdException;
import marxo.exception.ValidationException;
import marxo.security.MarxoAuthentication;
import marxo.tool.PasswordEncryptor;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKeyFactory;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.xml.bind.DatatypeConverter;
import java.util.List;

@Controller
@RequestMapping("user{:s?}")
public class UserController extends TenantChildController<User> {

	ApplicationContext securityContext = new ClassPathXmlApplicationContext("classpath*:security.xml");
	byte[] salt = DatatypeConverter.parseHexBinary((String) securityContext.getBean("passwordSaltHexString"));
	SecretKeyFactory secretKeyFactory = (SecretKeyFactory) securityContext.getBean("secretKeyFactory");
	PasswordEncryptor passwordEncryptor = new PasswordEncryptor(salt, secretKeyFactory);

	@Override
	public void preHandle() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		// Since create API might be accessed by an anonymous user, `authentication` might be AnonymousAuthentication.
		if (authentication instanceof MarxoAuthentication) {
			user = ((MarxoAuthentication) authentication).getUser();
		}
	}

	@Override
	public User create(@RequestBody User user, HttpServletResponse response) throws Exception {
		switch (user.type) {
			case ADMIN:
				throw new ValidationException(String.format("Cannot create user with type %s", user.type));
			case EVALUATOR:
			case PARTICIPANT:
				Assert.isNull(user.tenantId);
				break;
		}

		user.createUserId = user.updateUserId = user.id;
		user.createTime = user.updateTime = DateTime.now();
		user.setPassword(passwordEncryptor.encrypt(user.getPassword()));
		user.save();

		response.setHeader("Location", "/user/" + user.getEmail());
		user.clearPassword();
		return user;
	}

	@RequestMapping(value = "/{idString:[\\w\\-\\+\\.@]+}", method = RequestMethod.GET)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@Override
	public User read(@PathVariable String idString) throws Exception {
		User user;

		if (ObjectId.isValid(idString)) {
			ObjectId objectId = new ObjectId(idString);
			user = User.get(objectId);
		} else {
			if (!User.emailPattern.matcher(idString).find()) {
				throw new InvalidObjectIdException(idString);
			}

			String email = idString;
			user = User.getByEmail(email);
		}

		if (user == null) {
			throw new EntityNotFoundException(User.class, idString);
		}

		user.clearPassword();

		return user;
	}

	@RequestMapping(value = "/me", method = RequestMethod.GET)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public User getCurrentUser() throws Exception {
		user.clearPassword();
		return user;
	}

	@RequestMapping(value = "/{idString:[\\w\\-\\+\\.@]+}", method = RequestMethod.PUT)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@Override
	public User update(@PathVariable String idString, @Valid @RequestBody User user) throws Exception {
		Assert.notNull(user.getPassword());
		switch (user.type) {
			case ADMIN:
				throw new ValidationException(String.format("Cannot update user with type %s", user.type));
			case EVALUATOR:
			case PARTICIPANT:
				Assert.isNull(user.tenantId);
				break;
		}

		Query query;

		if (ObjectId.isValid(idString)) {
			ObjectId objectId = new ObjectId(idString);
			query = Query.query(Criteria.where("_id").is(objectId));
			user.id = objectId;
		} else if (!User.emailPattern.matcher(idString).find()) {
			throw new InvalidObjectIdException(idString);
		} else {
			query = Query.query(Criteria.where("email").is(idString));
			user.setEmail(idString);
		}

		User oldUser = mongoTemplate.findOne(query, User.class);

		if (oldUser == null) {
			throw new EntityExistsException(idString);
		}

		user.setPassword(passwordEncryptor.encrypt(user.getPassword()));
		user.save();

		user.clearPassword();
		return user;
	}

	@RequestMapping(value = "/{idString:[\\w\\-\\+\\.@]+}", method = RequestMethod.DELETE)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@Override
	public User delete(@PathVariable String idString) throws Exception {
		User user;
		Query query;

		if (ObjectId.isValid(idString)) {
			query = Query.query(Criteria.where("_id").is(new ObjectId(idString)));
		} else if (!User.emailPattern.matcher(idString).find()) {
			throw new InvalidObjectIdException(idString);
		} else {
			query = Query.query(Criteria.where("email").is(idString));
		}

		user = mongoTemplate.findAndRemove(query, User.class);

		if (user == null) {
			throw new EntityNotFoundException(User.class, idString);
		}

		user.clearPassword();

		return user;
	}

	@Override
	public List<User> search() {
		Criteria criteria1 = Criteria.where("tenantId").is(user.tenantId);
		Criteria criteria2 = Criteria.where("tenantId").exists(false);
		Criteria criteria = new Criteria().orOperator(criteria1, criteria2);
		return mongoTemplate.find(Query.query(criteria).with(defaultSort), User.class);
	}
}
