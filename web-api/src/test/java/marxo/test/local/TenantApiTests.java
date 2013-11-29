package marxo.test.local;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.net.MediaType;
import marxo.entity.user.Tenant;
import marxo.test.ApiTestConfiguration;
import marxo.test.BasicApiTests;
import marxo.test.Tester;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

@SuppressWarnings("unchecked")
@ApiTestConfiguration
public class TenantApiTests extends BasicApiTests {

	@Test
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
				if (tenant.id.equals(reusedUser.tenantId)) {
					doesContainThisUser = true;
					break;
				}
			}
			Assert.assertTrue(doesContainThisUser);
		}
	}
}