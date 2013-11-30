package marxo.test.local;

import com.google.common.net.MediaType;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.types.User;
import marxo.entity.FacebookData;
import marxo.entity.FacebookStatus;
import marxo.entity.user.Tenant;
import marxo.test.ApiTestConfiguration;
import marxo.test.BasicApiTests;
import marxo.test.Tester;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@ApiTestConfiguration(value = "http://localhost:8080/api/services/facebook")
public class FacebookApiTests extends BasicApiTests {
	static final String appId;
	static final String appSecret;
	static final String appToken;
	static final String userToken;
	static final FacebookClient facebookClient;
	static String extendedUserToken;

	static {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("security.xml");
		appId = (String) applicationContext.getBean("appId");
		appSecret = (String) applicationContext.getBean("appSecret");
		appToken = (String) applicationContext.getBean("appToken");
		userToken = "CAADCM9YpGYwBAGANsWfvdO3aEPcqWE8NM2AqeKZBjrjv3MquGBWMTDHBy8LKwd8klnZCigONqGubLv7ZAmX3dl5b2kmnx8b86ZAtK6XL63yb7BnxXd0OcYvZCjt6ZCINSd4wbdcwMzT3FHQfo91rAdWrKfSZBL47YDthTbmv1ZAbdZAZBdYaEkw6FG34gOL8P4qkkZD";
		facebookClient = new DefaultFacebookClient(userToken);
	}

	@BeforeClass
	@Override
	public void beforeClass() throws Exception {
		super.beforeClass();
	}

	@Test
	public void saveData() throws Exception {
		User user = facebookClient.fetchObject("me", User.class);

		FacebookData facebookData = new FacebookData();
		facebookData.accessToken = facebookToken;
		facebookData.status = FacebookStatus.CONNECTED;
		facebookData.userId = user.getId();
		facebookData.username = user.getUsername();

		reusedTenant.facebookData = facebookData;
		reusedTenant.save();

		try (Tester tester = new Tester().baseUrl(baseUrl).basicAuth(email, password)) {
			tester
					.httpPut(facebookData)
					.send();
			tester
					.isOk()
					.matchContentType(MediaType.JSON_UTF_8);

			FacebookData facebookData1 = tester.getContent(FacebookData.class);
			Assert.assertNotNull(facebookData1);
		}

		Tenant tenant = Tenant.get(reusedTenant.id);
		Assert.assertEquals(tenant.id, reusedTenant.id);
		Assert.assertEquals(tenant.facebookData.accessToken, reusedTenant.facebookData.accessToken);
	}

	@Test(dependsOnMethods = {"saveData"})
	public void readData() throws Exception {
		try (Tester tester = new Tester().baseUrl(baseUrl).basicAuth(email, password)) {
			tester
					.httpGet()
					.send();
			tester
					.isOk()
					.matchContentType(MediaType.JSON_UTF_8);

			FacebookData facebookData = tester.getContent(FacebookData.class);
			Assert.assertNotNull(facebookData);
		}
	}

	@Test(dependsOnMethods = {"readData"})
	public void removeData() throws Exception {
		try (Tester tester = new Tester().baseUrl(baseUrl).basicAuth(email, password)) {
			tester
					.httpDelete()
					.send();
			tester
					.isOk()
					.matchContentType(MediaType.JSON_UTF_8);

			FacebookData facebookData = tester.getContent(FacebookData.class);
			Assert.assertNotNull(facebookData);
		}

		Tenant tenant = Tenant.get(reusedTenant.id);
		Assert.assertEquals(tenant.id, reusedTenant.id);
		Assert.assertNull(tenant.facebookData);
	}
}
