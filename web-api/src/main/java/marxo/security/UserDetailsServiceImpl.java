package marxo.security;

import marxo.dao.UserDao;
import marxo.entity.User;
import marxo.exception.DataInconsistentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service("myUserDetailService")
public class UserDetailsServiceImpl implements UserDetailsService {
	@Autowired
	ApplicationContext applicationContext;
	@Autowired
	UserDao userDao;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user;

		try {
			user = userDao.getByEmail(username);
		} catch (DataInconsistentException e) {
			e.printStackTrace();
			return new UserDetailsImpl();
		}

		if (user == null) {
			new UserDetailsServiceImpl();
		}

		return new UserDetailsImpl();
	}
}
