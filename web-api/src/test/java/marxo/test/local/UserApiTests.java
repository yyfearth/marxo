package marxo.test.local;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.net.MediaType;
import marxo.test.ApiTestConfiguration;
import marxo.test.BasicApiTests;
import marxo.test.Tester;
import marxo.entity.user.Tenant;
import marxo.entity.user.User;
import marxo.exception.ErrorJson;
import org.springframework.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

@SuppressWarnings("unchecked")
@ApiTestConfiguration
public class UserApiTests extends BasicApiTests {

	@Test(groups = "authentication")
	public void getUser() throws Exception {
		try (Tester tester = new Tester().basicAuth(email, password)) {
			tester
					.httpGet(baseUrl + "users/" + email)
					.send();
			tester
					.isOk()
					.matchContentType(MediaType.JSON_UTF_8);
			user = tester.getContent(User.class);
			Assert.assertEquals(user.getEmail(), email);
			Assert.assertNotNull(user.id);
			Assert.assertNotNull(user.getName());
			Assert.assertNotNull(user.createdDate);
			Assert.assertNotNull(user.modifiedDate);
			Assert.assertNotNull(user.tenantId);
			Assert.assertNull(user.getPassword());
		}
	}

	@Test
	public void wrongAuthentication() throws Exception {
		try (Tester tester = new Tester()) {
			tester
					.httpGet(baseUrl)
					.basicAuth(email, "wrong password")
					.send();
			tester
					.is(HttpStatus.UNAUTHORIZED)
					.matchContentType(MediaType.JSON_UTF_8);
			ErrorJson errorJson = tester.getContent(ErrorJson.class);
			Assert.assertNotNull(errorJson);

			tester
					.httpGet(baseUrl)
					.basicAuth("wrong email", password)
					.send();
			tester
					.is(HttpStatus.UNAUTHORIZED)
					.matchContentType(MediaType.JSON_UTF_8);
			errorJson = tester.getContent(ErrorJson.class);
			Assert.assertNotNull(errorJson);

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

	@Test(dependsOnGroups = "authentication")
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

	@Test(dependsOnGroups = "authentication")
	public void testGetTenants() throws Exception {
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