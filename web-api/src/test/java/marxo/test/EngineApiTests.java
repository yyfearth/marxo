package marxo.test;

import com.google.common.net.MediaType;
import marxo.controller.EngineController;
import org.testng.Assert;
import org.testng.annotations.Test;

@ApiTestConfiguration("http://localhost:8080/api/engine/")
public class EngineApiTests extends BasicApiTests {

	@Test
	public void start() throws Exception {
		try (ApiTester apiTester = apiTesterBuilder.build()) {
			apiTester
					.httpPut("start", null)
					.send();
			apiTester
					.isOk()
					.matchContentType(MediaType.JSON_UTF_8);
			EngineController.Response response = apiTester.getContent(EngineController.Response.class);
			Assert.assertNotNull(response);
		}
	}

	@Test(dependsOnMethods = "start")
	public void getStatus() throws Exception {
		try (ApiTester apiTester = apiTesterBuilder.build()) {
			apiTester
					.httpGet("status")
					.send();
			apiTester
					.isOk()
					.matchContentType(MediaType.JSON_UTF_8);
			EngineController.Status status = apiTester.getContent(EngineController.Status.class);
			Assert.assertTrue(status.isAlive);
		}
	}

	@Test(dependsOnMethods = "getStatus")
	public void stop() throws Exception {
		try (ApiTester apiTester = apiTesterBuilder.build()) {
			apiTester
					.httpPut("stop", null)
					.send();
			apiTester
					.isOk()
					.matchContentType(MediaType.JSON_UTF_8);
			EngineController.Response response = apiTester.getContent(EngineController.Response.class);
			Assert.assertNotNull(response);
		}
	}
}
