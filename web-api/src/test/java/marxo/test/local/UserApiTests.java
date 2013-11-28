package marxo.test.local;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.net.MediaType;
import marxo.entity.user.Tenant;
import marxo.entity.user.User;
import marxo.exception.ErrorJson;
import marxo.test.ApiTestConfiguration;
import marxo.test.BasicApiTests;
import marxo.test.Tester;
import org.springframework.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

@SuppressWarnings("unchecked")
@ApiTestConfiguration
@Test(groups = "user")
public class UserApiTests extends BasicApiTests {

	@Test
	public void getOneUser() throws Exception {
		try (Tester tester = new Tester().basicAuth(email, password)) {
			tester
					.httpGet(baseUrl + "users/" + this.user.getEmail())
					.send();
			tester
					.isOk()
					.matchContentType(MediaType.JSON_UTF_8);
			User user = tester.getContent(User.class);
			Assert.assertEquals(user.getEmail(), this.user.getEmail());
			Assert.assertEquals(user.id, this.user.id);
			Assert.assertEquals(user.getName(), this.user.getName());
			Assert.assertEquals(user.createTime.toLocalDateTime(), this.user.createTime.toLocalDateTime());
			Assert.assertEquals(user.updateTime.toLocalDateTime(), this.user.updateTime.toLocalDateTime());
			Assert.assertEquals(user.tenantId, this.user.tenantId);
			Assert.assertNull(user.getPassword(), "One shouldn't get user's password via API");
		}
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

	@Test(dependsOnMethods = {"getOneUser", "wrongAuthentication"})
	public void getUsers() throws Exception {
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
				Assert.assertEquals(user.tenantId, this.user.tenantId);
			}
		}
	}

	@Test(dependsOnMethods = {"getOneUser", "wrongAuthentication"})
	public void getTenants() throws Exception {
		try (Tester tester = new Tester().basicAuth(email, password)) {
			tester
					.httpGet(baseUrl + "tenants")
					.send();
			tester
					.isOk()
					.matchContentType(MediaType.JSON_UTF_8);

			List<Tenant> tenants = tester.getContent(new TypeReference<List<Tenant>>() {
			});
			Assert.assertNotNull(tenants);
			boolean doesContainThisUser = false;
			for (Tenant tenant : tenants) {
				if (tenant.id.equals(user.tenantId)) {
					doesContainThisUser = true;
					break;
				}
			}
			Assert.assertTrue(doesContainThisUser);
		}
	}
}