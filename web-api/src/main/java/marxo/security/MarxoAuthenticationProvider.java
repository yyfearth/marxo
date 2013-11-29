package marxo.security;

import com.google.common.collect.Lists;
import marxo.entity.user.User;
import marxo.tool.Loggable;
import marxo.tool.PasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class MarxoAuthenticationProvider implements AuthenticationProvider, Loggable {
	protected static final ApplicationContext applicationContext = new ClassPathXmlApplicationContext("mongo-configuration.xml");
	protected static final MongoTemplate mongoTemplate = applicationContext.getBean(MongoTemplate.class);
	@Autowired
	PasswordEncryptor passwordEncryptor;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String email = authentication.getName();

		Criteria criteria = Criteria.where("email").is(email.toLowerCase());
		User user = mongoTemplate.findOne(Query.query(criteria), User.class);

		if (user == null) {
			String message = email + " is not found";
			throw new UsernameNotFoundException(message);
		}

		String plainPassword = authentication.getCredentials().toString();
		String encryptedPassword = passwordEncryptor.encrypt(plainPassword);

		if (user.getPassword() != null && encryptedPassword.toLowerCase().equals(user.getPassword().toLowerCase())) {
			return new MarxoAuthentication(user, Lists.newArrayList(new SimpleGrantedAuthority("user")));
		}

		String message = "The password '" + plainPassword + "' for '" + email + "' is not correct";
		throw new BadCredentialsException(message);
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return UsernamePasswordAuthenticationToken.class.equals(authentication);
	}
}