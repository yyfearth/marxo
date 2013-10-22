package marxo;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.types.User;
import org.testng.annotations.Test;

public class TestChannel {
	String accessToken = "CAACEdEose0cBADEuCrY8enxNnbC7umZBpkgBI5MAqFz0wFiYBlWUPV4AXoHtwgCt1QpREIeYbXaHIZCD5dVqMwMxtoN8tfiHFWQKsVH3FhLiagHMGf0TdClTIhwfbvTd5U204tlsdlwcp3zLGxww4EvWdYoF6nXvzOidnFW3JWRI3plrmOHWDeu2i4F5AZD";

	@Test
	public void testFacebook() throws Exception {
		FacebookClient facebookClient = new DefaultFacebookClient(accessToken);
		User user = facebookClient.fetchObject("me", User.class);
	}
}
