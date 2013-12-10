package marxo.security;

import com.google.common.collect.Lists;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.exception.FacebookOAuthException;
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
	static final String appId;
	static final String appSecret;
	static final String appToken;

	static {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("security.xml");
		appId = (String) applicationContext.getBean("appId");
		appSecret = (String) applicationContext.getBean("appSecret");
		appToken = (String) applicationContext.getBean("appToken");
	}

	protected static final ApplicationContext applicationContext = new ClassPathXmlApplicationContext("mongo-configuration.xml");
	protected static final MongoTemplate mongoTemplate = applicationContext.getBean(MongoTemplate.class);
	@Autowired
	PasswordEncryptor passwordEncryptor;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		User user;
		if (authentication.getName().toLowerCase().equals("facebook")) {
			String accessToken = authentication.getCredentials().toString();
			FacebookClient facebookClient = new DefaultFacebookClient(accessToken);
			try {
				com.restfb.types.User fbUser = facebookClient.fetchObject("me", com.restfb.types.User.class);
				Criteria criteria = Criteria.where("oAuthData.facebook").is(fbUser.getId());
				user = mongoTemplate.findOne(Query.query(criteria), User.class);
				if (user == null) {
					throw new UsernameNotFoundException(String.format("Cannot find anyone with the Facebook account %s(%s)", fbUser.getUsername(), fbUser.getId()));
				} else {
					return new MarxoAuthentication(user, Lists.newArrayList(new SimpleGrantedAuthority(user.type.toString())));
				}
			} catch (FacebookOAuthException e) {
				throw new BadCredentialsException(String.format("The access token %s is not valid: %s", accessToken, e.getMessage()));
			}
		}

		String email = authentication.getName();

		Criteria criteria = Criteria.where("email").is(email.toLowerCase());
		user = mongoTemplate.findOne(Query.query(criteria), User.class);

		if (user == null) {
			String message = email + " is not found";
			throw new UsernameNotFoundException(message);
		}

		String plainPassword = authentication.getCredentials().toString();
		String encryptedPassword = passwordEncryptor.encrypt(plainPassword);

		if (user.getPassword() != null && encryptedPassword.toLowerCase().equals(user.getPassword().toLowerCase())) {
			return new MarxoAuthentication(user, Lists.newArrayList(new SimpleGrantedAuthority("user")));
		}

		throw new BadCredentialsException(String.format("The password '%s' for '%s' is not correct", plainPassword, email));
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return UsernamePasswordAuthenticationToken.class.equals(authentication);
	}
}