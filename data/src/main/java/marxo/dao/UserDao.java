package marxo.dao;

import marxo.entity.user.User;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Repository
public class UserDao extends TenantChildDao<User> {
	public UserDao(ObjectId tenantId) {
		super(tenantId);
	}

	public UserDao() {
		super(null);
	}

	public List<User> getByEmail(String email) {
		email = email.toLowerCase();
		return find(new ArrayList<>(Arrays.asList(
				new DataPair("email", email)
		)));
	}
}
