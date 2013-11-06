package marxo.channel;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.exception.FacebookOAuthException;
import com.restfb.types.FacebookType;
import com.restfb.types.Post;
import com.restfb.types.User;
import marxo.tool.ILoggable;
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
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.ByteArrayOutputStream;

public class ChannelTests implements ILoggable {
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
		userToken = "CAADCM9YpGYwBACl64uziT6gUcc5kE8ZBrORcrKyMJp1VGZAqd2IZCLbCpgwBrFvUtGvDi1QyfSfIHEgyJ71TqdxXE4IGitZCvLtWVh4yJh5DYkCZBbBypn5NNJz02cZC7EUHmxwCcGYuVmk0JlAazdBi6160dgnta1zSpP2iNNOB4dp6eBZBvJtaZB9dGP4GyZBQZD";
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
			ILoggable.logger.info(objectMapper.writeValueAsString(user));
		} catch (FacebookOAuthException e) {
			ILoggable.logger.error(String.format("[%s] %s", e.getClass(), e.getMessage()), e);
		}
	}

	@Test
	public void getLongTermToken() {
		String requestToken = userToken;
		FacebookClient.AccessToken accessToken;
		DateTime dateTime;

		for (int i = 0; i < 2; i++) {
			ILoggable.logger.info("Getting extended token " + (i + 1));

			accessToken = facebookClient.obtainExtendedAccessToken(appId, appSecret, requestToken);
			extendedUserToken = accessToken.getAccessToken();
			ILoggable.logger.info("extendedUserToken: " + extendedUserToken);
			dateTime = new DateTime(accessToken.getExpires());
			ILoggable.logger.info("expire at " + dateTime);

			requestToken = accessToken.getAccessToken();
		}
	}

	@Test
	public void testHttpClient() throws Exception {
		CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet("http://google.com");

		try (CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpGet)) {
			ILoggable.logger.info(closeableHttpResponse.getStatusLine().toString());
			HttpEntity httpEntity = closeableHttpResponse.getEntity();

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			httpEntity.writeTo(outputStream);
			String content = outputStream.toString();
			ILoggable.logger.info("Content: " + content);

			EntityUtils.consume(httpEntity);
		}
	}

	@Test
	public void testDebugToken() throws Exception {
		CloseableHttpClient closeableHttpClient = HttpClients.createDefault();

		HttpGet httpGet = new HttpGet("");

		try (CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpGet)) {
			ILoggable.logger.info(closeableHttpResponse.getStatusLine().toString());
			HttpEntity httpEntity = closeableHttpResponse.getEntity();

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			httpEntity.writeTo(outputStream);
			String content = outputStream.toString();
			ILoggable.logger.info("Content: " + content);

			EntityUtils.consume(httpEntity);
		}
	}

	@Test
	public void testParseSignedRequest() throws Exception {
		String signedRequest = "";
		String data = parseSignedRequest(signedRequest);
		assert data != null;
	}

	public String parseSignedRequest(String signedRequest) {
//		list($encoded_sig, $payload) = explode('.', $signed_request, 2);
//
//		// decode the data
//		$sig = base64_url_decode($encoded_sig);
//		$data = json_decode(base64_url_decode($payload), true);
//
//		// confirm the signature
//		$expected_sig = hash_hmac('sha256', $payload, $secret, $raw = true);
//		if ($sig !== $expected_sig) {
//			error_log('Bad Signed JSON signature!');
//			return null;
//		}
//
//		return $data;
		throw new NotImplementedException();
	}

	@Test
	public void testSubmitPost() throws Exception {
		FacebookType publishMessageResponse = facebookClient.publish("me/feed", FacebookType.class, Parameter.with("message", "Marxo Test"));

		ILoggable.logger.info("Published message ID: " + publishMessageResponse.getId());
	}

	@Test()
	public void testGetPost() throws Exception {
		Post post = facebookClient.fetchObject("635503102_10152019328983103", Post.class);
		assert post != null;
		ILoggable.logger.info(objectMapper.writeValueAsString(post));
	}
}
