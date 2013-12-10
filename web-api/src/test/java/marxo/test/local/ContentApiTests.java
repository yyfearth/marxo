package marxo.test.local;

import com.google.common.net.MediaType;
import marxo.entity.action.Content;
import marxo.test.ApiTestConfiguration;
import marxo.test.ApiTester;
import marxo.test.BasicApiTests;
import org.bson.types.ObjectId;
import org.testng.Assert;
import org.testng.annotations.Test;

@ApiTestConfiguration
public class ContentApiTests extends BasicApiTests {
	Content reusedContent;

	@Test
	public void createContent() throws Exception {
		try (ApiTester apiTester = new ApiTester().basicAuth(email, password)) {
			Content content1 = new Content(Content.Type.FACEBOOK);
			reusedContent = content1;
			entitiesToRemove.add(content1);
			content1.actionId = new ObjectId();
			content1.message = "createContent";

			apiTester
					.httpPost(baseUrl + "content", content1)
					.send();
			apiTester
					.isCreated()
					.matchContentType(MediaType.JSON_UTF_8);
			Content content = apiTester.getContent(Content.class);
			Assert.assertNotNull(content);
			Assert.assertEquals(content.getName(), content1.getName());
		}
	}

	@Test(dependsOnMethods = "createContent")
	public void readContent() throws Exception {
		try (ApiTester apiTester = new ApiTester().basicAuth(email, password)) {
			apiTester
					.httpGet(baseUrl + "content/" + reusedContent.id)
					.send();
			apiTester
					.isOk()
					.matchContentType(MediaType.JSON_UTF_8);
			Content content = apiTester.getContent(Content.class);
			Assert.assertEquals(content.id, reusedContent.id);
		}
	}

	@Test(dependsOnMethods = "readContent")
	public void updateContent() throws Exception {
		try (ApiTester apiTester = new ApiTester().basicAuth(email, password)) {
			Content facebookContent = (Content) reusedContent;
			facebookContent.message = "updateContent";

			apiTester
					.httpPut(baseUrl + "content/" + reusedContent.id, facebookContent)
					.send();
			apiTester
					.isOk()
					.matchContentType(MediaType.JSON_UTF_8);
			Content content = apiTester.getContent(Content.class);
			Assert.assertNotNull(content);
			Assert.assertEquals(content.getName(), facebookContent.getName());
		}
	}

	@Test(dependsOnMethods = "updateContent")
	public void deleteContent() throws Exception {
		try (ApiTester apiTester = new ApiTester().basicAuth(email, password)) {
			Content facebookContent = new Content(Content.Type.FACEBOOK);
			entitiesToRemove.add(facebookContent);
			facebookContent.actionId = new ObjectId();
			facebookContent.message = "createContent";

			apiTester
					.httpDelete(baseUrl + "content/" + reusedContent.id)
					.send();
			apiTester
					.isOk()
					.matchContentType(MediaType.JSON_UTF_8);

			Content content = mongoTemplate.findById(reusedContent.id, Content.class);
			Assert.assertNull(content);
		}
	}
}
