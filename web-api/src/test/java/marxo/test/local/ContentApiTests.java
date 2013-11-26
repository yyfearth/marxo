package marxo.test.local;

import com.google.common.net.MediaType;
import marxo.entity.content.Content;
import marxo.entity.content.FacebookContent;
import marxo.test.ApiTestConfiguration;
import marxo.test.BasicApiTests;
import marxo.test.Tester;
import org.bson.types.ObjectId;
import org.testng.Assert;
import org.testng.annotations.Test;

@ApiTestConfiguration
public class ContentApiTests extends BasicApiTests {
	Content reusedContent;

	@Test
	public void testCreateContent() throws Exception {
		try (Tester tester = new Tester().basicAuth(email, password)) {
			FacebookContent facebookContent = new FacebookContent();
			reusedContent = facebookContent;
			entitiesToRemove.add(facebookContent);
			facebookContent.actionId = new ObjectId();
			facebookContent.message = "testCreateContent";

			tester
					.httpPost(baseUrl + "content", facebookContent)
					.send();
			tester
					.isCreated()
					.matchContentType(MediaType.JSON_UTF_8);
			Content content = tester.getContent(Content.class);
			Assert.assertNotNull(content);
			Assert.assertEquals(content.getName(), facebookContent.getName());
		}
	}

	@Test(dependsOnMethods = "testCreateContent")
	public void testReadContent() throws Exception {
		try (Tester tester = new Tester().basicAuth(email, password)) {
			tester
					.httpGet(baseUrl + "content/" + reusedContent.id)
					.send();
			tester
					.isOk()
					.matchContentType(MediaType.JSON_UTF_8);
			Content content = tester.getContent(Content.class);
			Assert.assertEquals(content.id, reusedContent.id);
		}
	}

	@Test(dependsOnMethods = "testReadContent")
	public void testUpdateContent() throws Exception {
		try (Tester tester = new Tester().basicAuth(email, password)) {
			FacebookContent facebookContent = (FacebookContent) reusedContent;
			facebookContent.message = "testUpdateContent";

			tester
					.httpPut(baseUrl + "content/" + reusedContent.id, facebookContent)
					.send();
			tester
					.isOk()
					.matchContentType(MediaType.JSON_UTF_8);
			Content content = tester.getContent(Content.class);
			Assert.assertNotNull(content);
			Assert.assertEquals(content.getName(), facebookContent.getName());
		}
	}

	@Test(dependsOnMethods = "testUpdateContent")
	public void testDeleteContent() throws Exception {
		try (Tester tester = new Tester().basicAuth(email, password)) {
			FacebookContent facebookContent = new FacebookContent();
			entitiesToRemove.add(facebookContent);
			facebookContent.actionId = new ObjectId();
			facebookContent.message = "testCreateContent";

			tester
					.httpDelete(baseUrl + "content/" + reusedContent.id)
					.send();
			tester
					.isOk()
					.matchContentType(MediaType.JSON_UTF_8);

			Content content = mongoTemplate.findById(reusedContent.id, Content.class);
			Assert.assertNull(content);
		}
	}
}
