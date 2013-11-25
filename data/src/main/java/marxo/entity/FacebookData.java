package marxo.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.exception.FacebookException;
import marxo.tool.Loggable;
import org.joda.time.DateTime;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({"service", "status", "user_id", "username", "fullname", "access_token", "expires_at", "link", "locale"})
public class FacebookData implements Loggable {
	static ApplicationContext applicationContext = new ClassPathXmlApplicationContext("security.xml");
	static String appId = (String) applicationContext.getBean("appId");
	static String appSecret = (String) applicationContext.getBean("appSecret");
	static String appToken = (String) applicationContext.getBean("appToken");

	public String service = "facebook";
	public FacebookStatus status = FacebookStatus.UNKNOWN;
	public String userId;
	public String username;
	public String fullName;
	public String accessToken;
	@JsonProperty("expires_at")
	public DateTime expireTime;
	public DateTime lastCheckTime;
	public String link;
	public String locale;

	public boolean hasExpired() {
		return (expireTime == null) || expireTime.isBeforeNow();
	}

	public boolean updateToken() {
		try {
			FacebookClient facebookClient = new DefaultFacebookClient(this.accessToken);
			FacebookClient.AccessToken accessToken = facebookClient.obtainExtendedAccessToken(appId, appSecret, this.accessToken);
			this.accessToken = accessToken.getAccessToken();
			expireTime = new DateTime(accessToken.getExpires());
			status = FacebookStatus.CONNECTED;
		} catch (FacebookException e) {
			logger.warn(String.format(
					"Fail to refresh Facebook access token.\n" +
							"Message: %s\n" +
							"Access token %s"
					, e.getMessage(), this.accessToken
			));
			this.lastCheckTime = DateTime.now();
			return false;
		}
		return true;
	}
}
