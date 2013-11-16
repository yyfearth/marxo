package marxo.channel;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.exception.FacebookOAuthException;
import com.restfb.types.FacebookType;
import com.restfb.types.Post;
import com.restfb.types.User;
import marxo.tool.Loggable;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.joda.time.DateTime;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import sun.misc.BASE64Decoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

public class ChannelTests implements Loggable {
	static String appId;
	static String appSecret;
	static String appToken;
	static String userToken;
	static String extendedUserToken;
	static String postId = "635503102_10152019328983103";

	static {
		ApplicationContext applicationContext = new AnnotationConfigApplicationContext(FacebookConfig.class);
		appId = (String) applicationContext.getBean("appId");
		appSecret = (String) applicationContext.getBean("appSecret");
		appToken = (String) applicationContext.getBean("appToken");
		userToken = "CAADCM9YpGYwBAGANsWfvdO3aEPcqWE8NM2AqeKZBjrjv3MquGBWMTDHBy8LKwd8klnZCigONqGubLv7ZAmX3dl5b2kmnx8b86ZAtK6XL63yb7BnxXd0OcYvZCjt6ZCINSd4wbdcwMzT3FHQfo91rAdWrKfSZBL47YDthTbmv1ZAbdZAZBdYaEkw6FG34gOL8P4qkkZD";
	}

	FacebookClient facebookClient = new DefaultFacebookClient(userToken);
	ObjectMapper objectMapper = new ObjectMapper();

	@BeforeClass
	public void beforeClass() throws Exception {

	}

	@Test
	public void getUser() throws Exception {
		User user = null;
		try {
			user = facebookClient.fetchObject("me", User.class);
			ObjectMapper objectMapper = new ObjectMapper();
			logger.info(objectMapper.writeValueAsString(user));
		} catch (FacebookOAuthException e) {
			logger.error(String.format("[%s] %s", e.getClass(), e.getMessage()), e);
			assert false;
		}
	}

	@Test
	public void getLongTermToken() {
		String requestToken = userToken;
		FacebookClient.AccessToken accessToken;
		DateTime dateTime;

		logger.info("Getting extended token");

		accessToken = facebookClient.obtainExtendedAccessToken(appId, appSecret, requestToken);
		extendedUserToken = accessToken.getAccessToken();
		logger.info("extendedUserToken: " + extendedUserToken);
		dateTime = new DateTime(accessToken.getExpires());
		logger.info("expire at " + dateTime);

		requestToken = accessToken.getAccessToken();
	}

	@Test
	public void testDebugToken() throws Exception {
		CloseableHttpClient closeableHttpClient = HttpClients.createDefault();

//		HttpGet httpGet = new HttpGet(String.format("https://graph.facebook.com/debug_token?input_token=%s&access_token=%s", userToken, appToken));
		URL url = new URL("https://graph.facebook.com/debug_token?input_token=CAADCM9YpGYwBAGANsWfvdO3aEPcqWE8NM2AqeKZBjrjv3MquGBWMTDHBy8LKwd8klnZCigONqGubLv7ZAmX3dl5b2kmnx8b86ZAtK6XL63yb7BnxXd0OcYvZCjt6ZCINSd4wbdcwMzT3FHQfo91rAdWrKfSZBL47YDthTbmv1ZAbdZAZBdYaEkw6FG34gOL8P4qkkZD&access_token=213527892138380|eZgwp-1kegwB-CI-pYi7Q2WllKw");
		URI uri = new URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery());
		HttpGet httpGet = new HttpGet(uri);

		try (CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpGet)) {
			logger.info(closeableHttpResponse.getStatusLine().toString());
			HttpEntity httpEntity = closeableHttpResponse.getEntity();

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			httpEntity.writeTo(outputStream);
			String content = outputStream.toString();
			logger.info("Content: " + content);

			EntityUtils.consume(httpEntity);
		}
	}

	@Test
	public void testParseSignedRequest() throws Exception {
		String signedRequestString = "FxxDQB7v5bYjmhJ7_ry4VmeKpJ5bpYKR6MSk-HX8hkg.eyJhbGdvcml0aG0iOiJITUFDLVNIQTI1NiIsImNvZGUiOiJBUUJNeXdEZVdtMFZFSmlPbFl6cHpid0lmR0JZcDdfT3o4MllSMGZDMTc5NXZqcTM2dXpBekIwR1J1TVhla050U1RKbDFTdFhqYm55anVEbUphd1JNUGhjVFZwUmNJV3pHNy1PSUYxM0NnQnRLYWNlSEFsbFpqcVFDT093R0o1TkxpYkVMUU9kVHB3Q0ZVSHhzNTRFNjFqdDNCZF9PQjBzbXU1SlBCb3RFRzEzUVQ0SW5KUGIzMXhaeHNRTEl4UThkbW1fbUE2RWdqWDgxMkxhbkphSmVSeWplZXRzWi1DaXR6QzlReGhOVXFyek90LTE4TG9hTlRjZmVBRnVEcWpwYnhGaVdIMjVSTjN3QWo0bFNYU25tZ1JCdkdISkl1YXFPSGVpTWFZbVVnZlp2SFpscWQ2TWRHcWc1VmJuZjdJLVRqWSIsImlzc3VlZF9hdCI6MTM4Mzc1OTc3NCwidXNlcl9pZCI6IjYzNTUwMzEwMiJ9";
		SignedRequest signedRequest = parseSignedRequest(signedRequestString);
		assert signedRequest != null;
		assert signedRequest.signiture != null;
		assert signedRequest.payload != null;
	}

	/**
	 * <a href="https://developers.facebook.com/docs/facebook-login/using-login-with-games/#checklogin">Official docs</a>
	 */
	public SignedRequest parseSignedRequest(String signedRequestString) throws IOException {
		String[] list = signedRequestString.split("\\.");
		BASE64Decoder base64Decoder = new BASE64Decoder();
		String sig = new String(base64Decoder.decodeBuffer(list[0]));
		String payload = new String(base64Decoder.decodeBuffer(list[1]));
		return new SignedRequest(sig, payload);
	}

	@Test
	public void testSubmitPost() throws Exception {
		FacebookType publishMessageResponse = facebookClient.publish("me/feed", FacebookType.class, Parameter.with("message", "Marxo Test, please do some action on this post. Thanks."));

		logger.info("Published message ID: " + publishMessageResponse.getId());
	}

	@Test()
	public void testGetPost() throws Exception {
		Post post = facebookClient.fetchObject(postId, Post.class);
		assert post != null;

		logger.info(objectMapper.writeValueAsString(post));
		assert post.getApplication().getName().equals("Marxo");
	}

	class SignedRequest {
		public String signiture;
		public String payload;

		SignedRequest(String signiture, String payload) {
			this.signiture = signiture;
			this.payload = payload;
		}
	}
}
