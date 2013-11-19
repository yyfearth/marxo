package marxo.security;

import marxo.dao.DaoContext;
import marxo.dao.UserDao;
import marxo.entity.user.User;
import marxo.tool.Loggable;
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
public class MarxoAuthenticationProvider implements AuthenticationProvider, Loggable {
	UserDao userDao;
	@Autowired
	PasswordEncryptor passwordEncryptor;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String email = authentication.getName();

		userDao = new UserDao();
		User user = userDao.findOne(DaoContext.newInstance().addContext("email", email.toLowerCase()));

		if (user == null) {
			String message = email + " is not found";
			throw new UsernameNotFoundException(message);
		}

		String plainPassword = authentication.getCredentials().toString();
		String encryptedPassword = passwordEncryptor.encrypt(plainPassword);

		if (encryptedPassword.toLowerCase().equals(user.getPassword().toLowerCase())) {
			List<GrantedAuthority> grantedAuths = new ArrayList<>();
			grantedAuths.add(new SimpleGrantedAuthority("user"));
			return new MarxoAuthentication(user, grantedAuths);
		}

		String message = "The password " + plainPassword + " for " + email + " is not correct";
		throw new BadCredentialsException(message);
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return UsernamePasswordAuthenticationToken.class.equals(authentication);
	}
}