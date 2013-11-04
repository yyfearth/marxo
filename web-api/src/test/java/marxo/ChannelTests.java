package marxo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.exception.FacebookOAuthException;
import com.restfb.types.User;
import marxo.tool.ILoggable;
import org.joda.time.DateTime;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.testng.annotations.Test;

public class ChannelTests implements ILoggable {
	static String appId;
	static String appSecret;
	static String appToken;
	static String userToken;
	static String extendedUserToken;

	static {
		ApplicationContext applicationContext = new AnnotationConfigApplicationContext(FacebookConfig.class);
		appId = (String) applicationContext.getBean("appId");
		appSecret = (String) applicationContext.getBean("appSecret");
		appToken = (String) applicationContext.getBean("appToken");
		userToken = "CAADCM9YpGYwBAI57YJyWayreUUAcAVE2xZCYViTzI7SaRiTVeCA4ui0cXlF7laNYAf5gN15ZCMugTGhhq8fqS7HW7vZCBye2tiqGdPWofCgtSqPJ3F9PI1hLqe5NZB6wloj7ej8zAJ5Vhdge2eblTXjeAcZBdG95u79N3vZBzEuLZC71YYt2l9uGP65zJXrjSYZD";
	}

	@Test
	public void getUser() throws Exception {
		FacebookClient facebookClient = new DefaultFacebookClient(userToken);
		User user = null;
		try {
			user = facebookClient.fetchObject("me", User.class);
			ObjectMapper objectMapper = new ObjectMapper();
			logger.info(objectMapper.writeValueAsString(user));
		} catch (FacebookOAuthException e) {
			logger.error(String.format("[%s] %s", e.getClass(), e.getMessage()), e);
		}
	}

	@Test
	public void getLongTermToken() {
		FacebookClient facebookClient = new DefaultFacebookClient(userToken);
		String requestToken = userToken;
		FacebookClient.AccessToken accessToken;
		DateTime dateTime;

		for (int i = 0; i < 2; i++) {
			logger.info("Getting extended token " + (i + 1));

			accessToken = facebookClient.obtainExtendedAccessToken(appId, appSecret, requestToken);
			extendedUserToken = accessToken.getAccessToken();
			logger.info("extendedUserToken: " + extendedUserToken.substring(0, 6) + "..." + extendedUserToken.substring(extendedUserToken.length() - 5));
			dateTime = new DateTime(accessToken.getExpires());
			logger.info("expire at " + dateTime);

			requestToken = accessToken.getAccessToken();
		}
	}
}
