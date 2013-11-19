package marxo.dao;

import marxo.entity.user.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserDao extends TenantChildDao<User> {
}
