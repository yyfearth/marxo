package marxo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.net.MediaType;
import marxo.entity.link.Link;
import marxo.entity.node.Node;
import marxo.entity.user.User;
import marxo.entity.workflow.Workflow;
import marxo.serialization.MarxoObjectMapper;
import marxo.tool.Loggable;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.junit.AfterClass;
import org.springframework.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public class WebApiTests implements Loggable {
	final String email = "yyfearth@gmail.com";
	final String password = "2k96H29ECsJ05BJAkEGm6FC+UgjwVTc1qOd7SGG2uS8";
	User user;
	String baseUrl = "http://localhost:8080/api/";
	//	String baseUrl = "http://masonwan.com/marxo/api/";
	List<Workflow> workflows = new ArrayList<>();

	@BeforeClass
	public void beforeClass() {
	}

	@AfterClass
	public void afterClass() throws IOException {
	}

	@BeforeMethod
	public void setUp() throws Exception {
	}

	@AfterMethod
	public void tearDown() throws Exception {
	}

	@Test(priority = -100, groups = "start")
	public void getUser() throws Exception {
		try (Tester tester = new Tester()) {
			tester
					.httpGet(baseUrl + "users/" + email)
					.basicAuth(email, password)
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

	@Test(dependsOnGroups = "start")
	public void getUsers() throws Exception {
		try (Tester tester = new Tester()) {
			tester
					.httpGet(baseUrl + "users")
					.basicAuth(email, password)
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

	@Test(dependsOnGroups = "start")
	public void getWorkflows() throws Exception {
		try (Tester tester = new Tester()) {
			tester
					.httpGet(baseUrl + "workflows")
					.basicAuth(email, password)
					.send();
			tester
					.isOk()
					.matchContentType(MediaType.JSON_UTF_8);

			workflows = tester.getContent(new TypeReference<List<Workflow>>() {
			});
			Assert.assertNotNull(workflows);
			for (Workflow workflow : workflows) {
				Assert.assertEquals(workflow.tenantId, this.user.tenantId);
				Assert.assertFalse(workflow.isProject);
			}
		}
	}

	@Test(dependsOnGroups = "start", dependsOnMethods = "getWorkflows")
	public void searchWorkflows() throws Exception {
		if (workflows.size() == 0) {
			try (Tester tester = new Tester()) {
				tester
						.httpGet(baseUrl + "workflows")
						.basicAuth(email, password)
						.send();
				tester
						.isOk()
						.matchContentType(MediaType.JSON_UTF_8);

				List<Workflow> workflows = tester.getContent(new TypeReference<List<Workflow>>() {
				});
				Assert.assertNotNull(workflows);
				Assert.assertEquals(workflows.size(), 0);
			}
		}

		Workflow workflow = workflows.get(0);

		try (Tester tester = new Tester()) {
			tester
					.httpGet(baseUrl + "workflows/" + workflow.id)
					.basicAuth(email, password)
					.send();
			tester
					.isOk()
					.matchContentType(MediaType.JSON_UTF_8);

			workflow = tester.getContent(new TypeReference<Workflow>() {
			});
			Assert.assertNotNull(workflow);
			Assert.assertEquals(workflow.nodeIds.size(), workflow.nodes.size());
			Assert.assertEquals(workflow.linkIds.size(), workflow.links.size());
			Assert.assertEquals(workflow.tenantId, this.user.tenantId);
		}
	}

	@Test(dependsOnGroups = "start")
	public void getProjects() throws Exception {
		try (Tester tester = new Tester()) {
			tester
					.httpGet(baseUrl + "projects")
					.basicAuth(email, password)
					.send();
			tester
					.isOk()
					.matchContentType(MediaType.JSON_UTF_8);

			List<Workflow> workflows = tester.getContent(new TypeReference<List<Workflow>>() {
			});
			Assert.assertNotNull(workflows);
			for (Workflow workflow : workflows) {
				Assert.assertEquals(workflow.tenantId, this.user.tenantId);
				Assert.assertTrue(workflow.isProject);
			}
		}
	}

	@Test(dependsOnGroups = "start")
	public void getNodes() throws Exception {
		try (Tester tester = new Tester()) {
			tester
					.httpGet(baseUrl + "node")
					.basicAuth(email, password)
					.send();
			tester
					.isOk()
					.matchContentType(MediaType.JSON_UTF_8);
			List<Node> nodes = tester.getContent(new TypeReference<List<Node>>() {
			});
			Assert.assertNotNull(nodes);
//			for (Node node : nodes) {
//				Assert.assertEquals(node.tenantId, this.user.tenantId);
//				for (Action action : node.actions) {
//					if (action.contextId != null) {
//						Assert.assertNotNull(action.contextType);
//					}
//				}
//			}
		}
	}

	@Test(dependsOnGroups = "start")
	public void getLinks() throws Exception {
		try (Tester tester = new Tester()) {
			tester
					.httpGet(baseUrl + "links/")
					.basicAuth(email, password)
					.send();
			tester
					.isOk()
					.matchContentType(MediaType.JSON_UTF_8);
			List<Link> links = tester.getContent(new TypeReference<List<Link>>() {
			});
			Assert.assertNotNull(links);
//			for (Link link : links) {
//				Assert.assertEquals(link.tenantId, this.user.tenantId);
//				if (link.condition != null) {
//					Assert.assertNotNull(link.condition.leftOperand);
//					Assert.assertNotNull(link.condition.leftOperandType);
//					Assert.assertNotNull(link.condition.rightOperand);
//					Assert.assertNotNull(link.condition.rightOperandType);
//				}
//			}
		}
	}
}

class Tester implements Closeable {
	HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
	CloseableHttpClient client = httpClientBuilder.build();
	CloseableHttpResponse response;
	MarxoObjectMapper objectMapper = new MarxoObjectMapper();
	URI uri;
	BasicHeader authorizationHeader;
	HttpRequestBase request;
	HttpEntity httpEntity;
	MediaType mediaType;
	String content;

	public Tester httpGet(String url) throws URISyntaxException {
		this.uri = new URI(url);
		request = new HttpGet(uri);
		return this;
	}

	public Tester basicAuth(String username, String password) {
		String credentialString = username + ":" + password;
		String credential = DatatypeConverter.printBase64Binary(credentialString.getBytes());
		authorizationHeader = new BasicHeader("Authorization", "Basic " + credential);
		request.addHeader(authorizationHeader);
		return this;
	}

	public Tester send() throws IOException {
		response = client.execute(request);
		httpEntity = response.getEntity();
		mediaType = MediaType.parse(httpEntity.getContentType().getValue());
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		httpEntity.writeTo(stream);
		content = new String(stream.toByteArray());
		return this;
	}

	public Tester isOk() {
		StatusLine statusLine = response.getStatusLine();
		Assert.assertEquals(statusLine.getStatusCode(), HttpStatus.OK.value(), "Status: " + statusLine.getReasonPhrase() + " Message: " + content);
		return this;
	}

	public Tester matchContentType(MediaType mediaType) {
		assert this.mediaType.type().equals(mediaType.type());
		assert this.mediaType.subtype().equals(mediaType.subtype());
		return this;
	}

	public String getContent() {
		return content;
	}

	public <T> T getContent(Class<T> tClass) throws IOException {
		return objectMapper.readValue(content, tClass);
	}

	/**
	 * Use this to get content in class with generic type.
	 */
	public <T> T getContent(TypeReference<T> typeReference) throws IOException {
		return objectMapper.readValue(content, typeReference);
	}

	@Override
	public void close() throws IOException {
		if (client != null) {
			client.close();
		}
		if (response != null) {
			response.close();
		}
	}
}
