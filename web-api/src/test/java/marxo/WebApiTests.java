package marxo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.MediaType;
import marxo.entity.Workflow;
import org.apache.http.Header;
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
public class WebApiTests {
	HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
	ObjectMapper objectMapper = new ObjectMapper();
	CloseableHttpClient client;

	@BeforeClass
	public void beforeClass() {
		client = httpClientBuilder.build();
	}

	@AfterClass
	public void afterClass() throws IOException {
		client.close();
	}

	@BeforeMethod
	public void setUp() throws Exception {
	}

	@AfterMethod
	public void tearDown() throws Exception {
	}

	@Test
	public void getWorkflows() throws Exception {
		HttpGet request = new HttpGet("http://localhost:8080/api/workflows");
		Header header = getAuthorizationHeader("yyfearth@gmail.com", "2k96H29ECsJ05BJAkEGm6FC+UgjwVTc1qOd7SGG2uS8");

		try (Tester tester = new Tester()) {
			tester.httpGet("http://localhost:8080/api/workflows").basicAuth("yyfearth@gmail.com", "2k96H29ECsJ05BJAkEGm6FC+UgjwVTc1qOd7SGG2uS8").send();
			tester.isOk().matchContentType(MediaType.JSON_UTF_8);
			String content = tester.getContent();
			List<Workflow> workflows = objectMapper.readValue(content, ArrayList.class);
			assert workflows != null;
		}
	}

	public BasicHeader getAuthorizationHeader(String username, String password) {
		String credentialString = username + ":" + password;
		String credential = DatatypeConverter.printBase64Binary(credentialString.getBytes());
		return new BasicHeader("Authorization", "Basic " + credential);
	}
}

class Tester implements Closeable {
	HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
	CloseableHttpClient client = httpClientBuilder.build();
	CloseableHttpResponse response;
	ObjectMapper objectMapper = new ObjectMapper();
	URI uri;
	BasicHeader authorizationHeader;
	HttpRequestBase request;
	HttpEntity httpEntity;
	MediaType mediaType;

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
		return this;
	}

	public Tester isOk() {
		StatusLine statusLine = response.getStatusLine();
		Assert.assertEquals(statusLine.getStatusCode(), HttpStatus.OK.value(), "Returns " + statusLine.getReasonPhrase());
		return this;
	}

	public Tester matchContentType(MediaType mediaType) {
		assert this.mediaType.type().equals(mediaType.type());
		assert this.mediaType.subtype().equals(mediaType.subtype());
		return this;
	}

	public String getContent() throws IOException {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		httpEntity.writeTo(stream);
		return new String(stream.toByteArray());
	}

	@Override
	public void close() throws IOException {
		client.close();
		response.close();
	}
}
