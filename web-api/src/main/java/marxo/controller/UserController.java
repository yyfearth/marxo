package marxo.controller;

import marxo.entity.user.User;
import marxo.exception.EntityNotFoundException;
import marxo.exception.InvalidObjectIdException;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("user{:s?}")
public class UserController extends TenantChildController<User> {
	@RequestMapping(value = "/{idString:[\\w\\-\\+\\.@]+}", method = RequestMethod.GET)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@Override
	public User read(@PathVariable String idString) throws Exception {
		if (ObjectId.isValid(idString)) {
			return super.read(idString);
		}

		if (!User.emailPattern.matcher(idString).find()) {
			throw new InvalidObjectIdException(idString);
		}

		User user = mongoTemplate.findOne(Query.query(criteria.and("email").is(idString)), entityClass);
		if (user == null) {
			throw new EntityNotFoundException(User.class, idString);
		}

		return user;
	}
}
