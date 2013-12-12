package marxo.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Strings;
import com.google.common.net.MediaType;
import marxo.serialization.MarxoObjectMapper;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.springframework.http.HttpStatus;
import org.testng.Assert;

import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

// todo: create a builder for this
public class ApiTester implements Closeable {
	HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
	CloseableHttpClient client = httpClientBuilder.build();
	CloseableHttpResponse response;
	MarxoObjectMapper objectMapper = new MarxoObjectMapper();
	String baseUrl;
	URI uri;
	BasicHeader authorizationHeader;
	HttpRequestBase request;
	HttpEntity httpEntity;
	MediaType mediaType;
	String content;
	Pattern absolutePathPattern = Pattern.compile("^https?:\\/\\/", Pattern.CASE_INSENSITIVE);

	/*
	Method
	 */

	public ApiTester baseUrl(String url) throws URISyntaxException {
		this.baseUrl = url;
		return this;
	}

	public ApiTester httpGet(String url) throws URISyntaxException {
		setUri(url);
		request = new HttpGet(uri);
		setAuthHeader();
		return this;
	}

	public ApiTester httpGet() throws URISyntaxException {
		setUri("");
		request = new HttpGet(uri);
		setAuthHeader();
		return this;
	}

	public ApiTester httpPost(String url, String content) throws URISyntaxException, UnsupportedEncodingException {
		setUri(url);

		HttpPost httpPost = new HttpPost(uri);
		if (!Strings.isNullOrEmpty(content)) {
			StringEntity stringEntity = new StringEntity(content);
			httpPost.setEntity(stringEntity);
		}
		httpPost.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.JSON_UTF_8.toString());
		request = httpPost;
		setAuthHeader();

		return this;
	}

	public ApiTester httpPost(String url, Object o) throws JsonProcessingException, UnsupportedEncodingException, URISyntaxException {
		return httpPost(url, objectMapper.writeValueAsString(o));
	}

	public ApiTester httpPost(Object o) throws JsonProcessingException, UnsupportedEncodingException, URISyntaxException {
		return httpPost("", objectMapper.writeValueAsString(o));
	}

	public ApiTester httpPut(String url, String content) throws URISyntaxException, UnsupportedEncodingException {
		setUri(url);

		HttpPut httpPut = new HttpPut(uri);
		if (!Strings.isNullOrEmpty(content)) {
			StringEntity stringEntity = new StringEntity(content);
			httpPut.setEntity(stringEntity);
		}
		httpPut.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.JSON_UTF_8.toString());
		request = httpPut;
		setAuthHeader();

		return this;
	}

	public ApiTester httpPut(String url, Object o) throws URISyntaxException, UnsupportedEncodingException, JsonProcessingException {
		return httpPut(url, objectMapper.writeValueAsString(o));
	}

	public ApiTester httpPut(Object o) throws URISyntaxException, UnsupportedEncodingException, JsonProcessingException {
		return httpPut("", objectMapper.writeValueAsString(o));
	}

	public ApiTester httpDelete(String url) throws URISyntaxException, UnsupportedEncodingException {
		setUri(url);
		request = new HttpDelete(uri);
		setAuthHeader();
		return this;
	}

	public ApiTester httpDelete() throws URISyntaxException, UnsupportedEncodingException {
		setUri("");
		request = new HttpDelete(uri);
		setAuthHeader();
		return this;
	}

	protected void setUri(String url) {
		if (absolutePathPattern.matcher(url).find()) {
			this.uri = URI.create(url);
		} else {
			this.uri = URI.create(baseUrl + url);
		}
	}

	/*
	Auth
	 */

	public ApiTester basicAuth(String username, String password) {
		String credentialString = username + ":" + password;
		String credential = DatatypeConverter.printBase64Binary(credentialString.getBytes());
		authorizationHeader = new BasicHeader("Authorization", "Basic " + credential);
		setAuthHeader();
		return this;
	}

	private void setAuthHeader() {
		if (request != null) {
			request.addHeader(authorizationHeader);
		}
	}

	public ApiTester send() throws IOException {
		if (response != null) {
			response.close();
		}
		response = client.execute(request);
		httpEntity = response.getEntity();
		if (httpEntity == null || httpEntity.getContentType() == null) {
			content = "";
		} else {
			mediaType = MediaType.parse(httpEntity.getContentType().getValue());
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			httpEntity.writeTo(stream);
			content = new String(stream.toByteArray());
		}
		return this;
	}

	/*
	Validation
	 */

	public ApiTester is(HttpStatus httpStatus) {
		StatusLine statusLine = response.getStatusLine();
		Assert.assertEquals(statusLine.getStatusCode(), httpStatus.value(), "Status: " + statusLine.getReasonPhrase() + "\nMessage: " + content);
		return this;
	}

	public ApiTester isOk() {
		return is(HttpStatus.OK);
	}

	public ApiTester isNotFound() {
		return is(HttpStatus.NOT_FOUND);
	}

	public ApiTester isBadRequest() {
		return is(HttpStatus.BAD_REQUEST);
	}

	public ApiTester isCreated() {
		return is(HttpStatus.CREATED);
	}

	public ApiTester matchContentType(MediaType mediaType) {
		if (this.mediaType == mediaType) {
			return this;
		}
		Assert.assertEquals(this.mediaType.type(), mediaType.type());
		Assert.assertEquals(this.mediaType.subtype(), mediaType.subtype());
		return this;
	}

	public ApiTester matchHeader(String name, String value) {
		Header header = response.getFirstHeader(name);
		Assert.assertNotNull(header);
		Assert.assertEquals(value, header.getValue());
		return this;
	}

	/*
	Content
	 */

	public String getContent() {
		return content;
	}

	public <T> T getContent(Class<T> tClass) throws IOException {
		return objectMapper.readValue(content, tClass);
	}

	/**
	 * Use this to get content in class with generic type. Yeah, fuck the type erasure.
	 */
	public <T> T getContent(TypeReference<T> typeReference) throws IOException {
		return objectMapper.readValue(content, typeReference);
	}

	/*
	Others
	 */

	public CloseableHttpResponse getResponse() {
		return response;
	}

	public Header getHeader(String name) {
		return response.getFirstHeader(name);
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
