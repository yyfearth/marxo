package marxo.dao;

import marxo.entity.User;
import marxo.exception.DataInconsistentException;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserDao extends BasicDao<User> {
	public User getByEmail(String email) throws DataInconsistentException {
		email = email.toLowerCase();
		List<User> users = mongoTemplate.find(Query.query(Criteria.where("email").is(email)), User.class);

		if (users.size() >= 2) {
			throw new DataInconsistentException(users.size() + " users have the same email address (" + email + ").");
		}

		return (users.size() == 0) ? null : users.get(0);
	}
}
