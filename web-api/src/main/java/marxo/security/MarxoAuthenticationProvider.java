package marxo.security;

import marxo.dao.UserDao;
import marxo.entity.User;
import marxo.tool.PasswordEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MarxoAuthenticationProvider implements AuthenticationProvider {
	static final Logger logger = LoggerFactory.getLogger(MarxoAuthenticationProvider.class);
	@Autowired
	UserDao userDao;
	@Autowired
	PasswordEncryptor passwordEncryptor;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String email = authentication.getName();

		List<User> users = userDao.getByEmail(email);

		if (users.size() == 0) {
			throw new UsernameNotFoundException(email + " is not found");
		} else if (users.size() > 1) {
			// review: consider whether this is a bug.
			logger.warn("There are " + users.size() + " have email as " + email);
		}

		User user = users.get(0);
		String plainPassword = authentication.getCredentials().toString();
		String encryptedPassword = passwordEncryptor.encrypt(plainPassword);

		if (encryptedPassword.toLowerCase().equals(user.getPassword().toLowerCase())) {
			List<GrantedAuthority> grantedAuths = new ArrayList<>();
			grantedAuths.add(new SimpleGrantedAuthority("user"));
			return new MarxoAuthentication(user, grantedAuths);
		}

		throw new BadCredentialsException("The password for " + email + " is not correct");
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}
}