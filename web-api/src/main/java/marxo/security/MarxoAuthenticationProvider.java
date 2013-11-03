package marxo.security;

import marxo.dao.UserDao;
import marxo.entity.User;
import marxo.tool.ILoggable;
import marxo.tool.PasswordEncryptor;
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
public class MarxoAuthenticationProvider implements AuthenticationProvider, ILoggable {
	@Autowired
	UserDao userDao;
	@Autowired
	PasswordEncryptor passwordEncryptor;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String email = authentication.getName();

		List<User> users = userDao.getByEmail(email);

		if (users.size() == 0) {
			String message = email + " is not found";
			logger.trace(message);
			throw new UsernameNotFoundException(message);
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
			logger.trace(String.format("User [%s] passed BasicAuth", user.getEmail()));
			return new MarxoAuthentication(user, grantedAuths);
		}

		String message = "The password for " + email + " is not correct";
		logger.trace(message);
		throw new BadCredentialsException(message);
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}
}