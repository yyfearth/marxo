package marxo.controller;

import marxo.dao.UserDao;
import marxo.entity.User;
import marxo.exception.EntityNotFoundException;
import marxo.exception.InvalidObjectIdException;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("user{:s?}")
public class UserController extends TenantChildController<User> {
	UserDao userDao;

	@Autowired
	protected UserController(UserDao userDao) {
		super(userDao);
		this.userDao = userDao;
	}

	@RequestMapping(value = "/{idString:[\\w\\-\\+\\.@]+}", method = RequestMethod.GET)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@Override
	public User read(@PathVariable String idString) {
		if (ObjectId.isValid(idString)) {
			return super.read(idString);
		}

		if (!User.emailPattern.matcher(idString).find()) {
			throw new InvalidObjectIdException(idString);
		}

		List<User> users = userDao.getByEmail(idString);

		if (users.size() == 0) {
			throw new EntityNotFoundException(idString);
		}
		return users.get(0);
	}

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public List<User> search() {
		return dao.findAll();
	}
}
