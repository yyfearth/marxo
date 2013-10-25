package marxo.dao;

import marxo.entity.User;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserDao extends BasicDao<User> {
	public List<User> getByEmail(String email) {
		return mongoTemplate.find(Query.query(Criteria.where("email").is(email.toLowerCase())), User.class);
	}
}
