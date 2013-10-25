package marxo.security;

import marxo.dao.UserDao;
import marxo.entity.User;
import marxo.tool.PasswordEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKeyFactory;
import javax.xml.bind.DatatypeConverter;
import java.util.ArrayList;
import java.util.List;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {
	static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationProvider.class);
	static ApplicationContext securityContext;
	static PasswordEncryptor passwordEncryptor;

	static {
		securityContext = new ClassPathXmlApplicationContext("classpath*:security.xml");
		byte[] salt = DatatypeConverter.parseHexBinary((String) securityContext.getBean("passwordSaltHexString"));
		SecretKeyFactory secretKeyFactory = (SecretKeyFactory) securityContext.getBean("secretKeyFactory");
		passwordEncryptor = new PasswordEncryptor(salt, secretKeyFactory);
	}

	@Autowired
	UserDao userDao;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String email = authentication.getName();

		List<User> users = userDao.getByEmail(email);

		if (users.size() == 0) {
			throw new UsernameNotFoundException(email + " is not found");
		} else if (users.size() > 1) {
			// review: ask whether this is a bug.
			logger.warn("There are " + users.size() + " have email as " + email);
		}

		User user = users.get(0);
		String plainPassword = authentication.getCredentials().toString();
		String encryptedPassword = passwordEncryptor.encrypt(plainPassword);

		if (encryptedPassword.equals(user.password)) {
			List<GrantedAuthority> grantedAuths = new ArrayList<>();
			grantedAuths.add(new SimpleGrantedAuthority("user"));
			return new UsernamePasswordAuthenticationToken(user.email, user.password, grantedAuths);
		}

		throw new BadCredentialsException("The password for " + email + " is not correct");
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}
}