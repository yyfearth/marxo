package marxo;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.types.User;
import org.testng.annotations.Test;

public class WebApiTest {
	@Test
	public void canLinkWithFacebook() throws Exception {
		String accessToken = "CAACEdEose0cBAGdWqdr7bTlgQZBmIZANTa6cUlZAirlZBrIZAH2TzXYWdDE66bh2XnpRn1WZCZCqzYZBgpKu0axaiT7ICkH4pWfWOmdZCZBsoPlQaPvXg9bh9vYT4ihYVTUFZA4w9FiZBELmMvgNVU6xbz6Y8GA3PIb3DFulYLLR7q3hzTSximfUQJw2GvL7B0oJSpkZD";
		FacebookClient facebookClient = new DefaultFacebookClient(accessToken);
		User user = facebookClient.fetchObject("me", User.class);
	}
}
