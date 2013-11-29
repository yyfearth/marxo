package marxo.test.local;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Maps;
import com.google.common.net.MediaType;
import marxo.entity.user.User;
import marxo.entity.user.UserType;
import marxo.exception.ErrorJson;
import marxo.serialization.MarxoObjectMapper;
import marxo.test.ApiTestConfiguration;
import marxo.test.BasicApiTests;
import marxo.test.Tester;
import org.springframework.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

@SuppressWarnings("unchecked")
@ApiTestConfiguration
public class UserApiTests extends BasicApiTests {
	MarxoObjectMapper marxoObjectMapper = new MarxoObjectMapper();
	User publisher;
	User participant;

	@BeforeClass
	@Override
	public void beforeClass() throws Exception {
		super.beforeClass();

		publisher = new User();
		publisher.setName("New " + UserType.PUBLISHER);
		publisher.setEmail("publisher@test.com");
		publisher.setPassword("test");
		publisher.type = UserType.PUBLISHER;
		publisher.tenantId = reusedUser.tenantId;
		publisher.oAuthData = Maps.newHashMap();
		publisher.oAuthData.put("facebook", "287762482");
		entitiesToRemove.add(publisher);

		participant = new User();
		participant.setName("New " + UserType.PARTICIPANT);
		participant.setEmail("participant@test.com");
		participant.setPassword("test");
		participant.type = UserType.PARTICIPANT;
		participant.oAuthData.put("facebook", facebookId);
		entitiesToRemove.add(participant);
	}

	@Test
	public void searchUsers() throws Exception {
		try (Tester tester = new Tester().basicAuth(email, password)) {
			tester
					.httpGet(baseUrl + "users")
					.send();
			tester
					.isOk()
					.matchContentType(MediaType.JSON_UTF_8);

			List<User> users = tester.getContent(new TypeReference<List<User>>() {
			});
			Assert.assertNotNull(users);
			for (User user : users) {
				if (user.tenantId != null) {
					Assert.assertEquals(user.tenantId, this.reusedUser.tenantId);
				}
			}
		}
	}

	@Test
	public void getCurrentUser() throws Exception {
		try (Tester tester = new Tester().baseUrl(baseUrl + "users/me").basicAuth(email, password)) {
			tester
					.httpGet()
					.send();
			tester
					.isOk()
					.matchContentType(MediaType.JSON_UTF_8);

			User user = tester.getContent(User.class);
			Assert.assertNotNull(user);
			Assert.assertEquals(user.getEmail(), email);
		}
	}

	@Test
	public void createPublisher() throws Exception {

		try (Tester tester = new Tester().baseUrl(baseUrl + "users").basicAuth(email, password)) {
			tester
					.httpPost(publisher)
					.send();
			tester
					.isCreated()
					.matchContentType(MediaType.JSON_UTF_8);

			User user1 = tester.getContent(User.class);
			Assert.assertNotNull(user1);
			Assert.assertEquals(user1.getEmail(), publisher.getEmail());
			Assert.assertNull(user1.getPassword(), "One shouldn't get user's password via API");
			Assert.assertNotNull(user1.oAuthData);
			Assert.assertEquals(marxoObjectMapper.writeValueAsString(user1.oAuthData), marxoObjectMapper.writeValueAsString(publisher.oAuthData));
		}

		User user1 = User.get(publisher.id);
		Assert.assertNotNull(user1);
		Assert.assertEquals(user1.getEmail(), publisher.getEmail());
		Assert.assertNotNull(user1.getPassword());
		Assert.assertNotNull(user1.oAuthData);
		Assert.assertEquals(marxoObjectMapper.writeValueAsString(user1.oAuthData), marxoObjectMapper.writeValueAsString(publisher.oAuthData));
	}

	@Test
	public void createParticipant() throws Exception {

		try (Tester tester = new Tester().baseUrl(baseUrl + "users")) {
			tester
					.httpPost(participant)
					.send();
			tester
					.isCreated()
					.matchContentType(MediaType.JSON_UTF_8);

			User user1 = tester.getContent(User.class);
			Assert.assertNotNull(user1);
			Assert.assertEquals(user1.getEmail(), participant.getEmail());
			Assert.assertNull(user1.getPassword(), "One shouldn't get user's password via API");
			Assert.assertNotNull(user1.oAuthData);
			Assert.assertEquals(marxoObjectMapper.writeValueAsString(user1.oAuthData), marxoObjectMapper.writeValueAsString(participant.oAuthData));
		}

		User user1 = User.get(participant.id);
		Assert.assertNotNull(user1);
		Assert.assertEquals(user1.getEmail(), participant.getEmail());
		Assert.assertNotNull(user1.getPassword());
		Assert.assertNotNull(user1.oAuthData);
		Assert.assertEquals(marxoObjectMapper.writeValueAsString(user1.oAuthData), marxoObjectMapper.writeValueAsString(participant.oAuthData));
	}

	@Test(dependsOnMethods = {"createPublisher"})
	public void getPublisher() throws Exception {
		try (Tester tester = new Tester().basicAuth(email, password)) {
			tester
					.httpGet(baseUrl + "users/" + publisher.getEmail())
					.send();
			tester
					.isOk()
					.matchContentType(MediaType.JSON_UTF_8);
			User user = tester.getContent(User.class);
			Assert.assertEquals(user.getEmail(), publisher.getEmail());
			Assert.assertNull(user.getPassword(), "One shouldn't get user's password via API");
		}
	}

	@Test(dependsOnMethods = {"getPublisher"})
	public void udpateUser() throws Exception {
		User user = User.getByEmail(publisher.getEmail());
		user.setPassword(publisher.getPassword());
		user.setName("Updated user");

		try (Tester tester = new Tester().basicAuth(email, password)) {
			tester
					.httpPut(baseUrl + "users/" + publisher.getEmail(), user)
					.send();
			tester
					.isOk()
					.matchContentType(MediaType.JSON_UTF_8);

			User user1 = tester.getContent(User.class);
			Assert.assertNotNull(user1);
			Assert.assertEquals(user1.getName(), user.getName());
			Assert.assertNull(user1.getPassword(), "One shouldn't get user's password via API");
		}

		User user1 = User.getByEmail(publisher.getEmail());
		Assert.assertEquals(user1.getName(), user.getName());
	}

	@Test(dependsOnMethods = {"udpateUser"})
	public void deleteUser() throws Exception {
		try (Tester tester = new Tester().basicAuth(email, password)) {
			tester
					.httpDelete(baseUrl + "users/" + publisher.getEmail())
					.send();
			tester
					.isOk()
					.matchContentType(MediaType.JSON_UTF_8);

			User user = tester.getContent(User.class);
			Assert.assertNotNull(user);
			Assert.assertEquals(user.getEmail(), publisher.getEmail());
			Assert.assertNull(user.getPassword(), "One shouldn't get user's password via API");
		}

		User user = User.getByEmail(publisher.getEmail());
		Assert.assertNull(user);
	}

	@Test
	public void wrongAuthentication() throws Exception {
		// Note that it seems that the tester must be created again in order to prevent the remote server returns Bad Request response after the first request.
		ErrorJson errorJson;

		try (Tester tester = new Tester()) {
			tester
					.httpGet(baseUrl)
					.basicAuth(email, "wrong password")
					.send();
			tester
					.is(HttpStatus.UNAUTHORIZED)
					.matchContentType(MediaType.JSON_UTF_8);
			errorJson = tester.getContent(ErrorJson.class);
			Assert.assertNotNull(errorJson);
		}

		try (Tester tester = new Tester()) {
			tester
					.httpGet(baseUrl)
					.basicAuth("wrong email", password)
					.send();
			tester
					.is(HttpStatus.UNAUTHORIZED)
					.matchContentType(MediaType.JSON_UTF_8);
			errorJson = tester.getContent(ErrorJson.class);
			Assert.assertNotNull(errorJson);
		}

		try (Tester tester = new Tester()) {
			tester
					.httpGet(baseUrl)
					.basicAuth("", "")
					.send();
			tester
					.is(HttpStatus.UNAUTHORIZED)
					.matchContentType(MediaType.JSON_UTF_8);
			errorJson = tester.getContent(ErrorJson.class);
			Assert.assertNotNull(errorJson);
		}
	}

	@Test(dependsOnMethods = {"createParticipant"})
	public void basicAuthWithFacebookAcessToken() throws Exception {
		try (Tester tester = new Tester().baseUrl(baseUrl + "user/me").basicAuth("facebook", facebookToken)) {
			tester
					.httpGet()
					.send();
			tester
					.isOk()
					.matchContentType(MediaType.JSON_UTF_8);

			User user = tester.getContent(User.class);
			Assert.assertNotNull(user);
			Assert.assertEquals(user.id, participant.id);
		}
	}

	@Test
	public void basicAuthWithWrongFacebookAcessToken() throws Exception {
		try (Tester tester = new Tester().baseUrl(baseUrl + "user/me").basicAuth("facebook", reusedUser.oAuthData.get("facebook"))) {
			tester
					.httpGet()
					.send();
			tester
					.is(HttpStatus.UNAUTHORIZED)
					.matchContentType(MediaType.JSON_UTF_8);

			ErrorJson errorJson = tester.getContent(ErrorJson.class);
			Assert.assertNotNull(errorJson);
		}
	}
}