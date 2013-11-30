package marxo.test.local;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.net.MediaType;
import marxo.entity.user.Tenant;
import marxo.test.ApiTestConfiguration;
import marxo.test.ApiTester;
import marxo.test.BasicApiTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

@SuppressWarnings("unchecked")
@ApiTestConfiguration
public class TenantApiTests extends BasicApiTests {

	@Test
	public void getTenants() throws Exception {
		try (ApiTester apiTester = new ApiTester().basicAuth(email, password)) {
			apiTester
					.httpGet(baseUrl + "tenants")
					.send();
			apiTester
					.isOk()
					.matchContentType(MediaType.JSON_UTF_8);

			List<Tenant> tenants = apiTester.getContent(new TypeReference<List<Tenant>>() {
			});
			Assert.assertNotNull(tenants);
			boolean doesContainThisUser = false;
			for (Tenant tenant : tenants) {
				if (tenant.id.equals(reusedUser.tenantId)) {
					doesContainThisUser = true;
					break;
				}
			}
			Assert.assertTrue(doesContainThisUser);
		}
	}
}