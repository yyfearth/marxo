package marxo.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Strings;
import com.google.common.net.MediaType;
import marxo.serialization.MarxoObjectMapper;
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

public class Tester implements Closeable {
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

	public Tester baseUrl(String url) throws URISyntaxException {
		this.baseUrl = url;
		return this;
	}

	public Tester httpGet(String url) throws URISyntaxException {
		setUri(url);
		request = new HttpGet(uri);
		setAuthHeader();
		return this;
	}

	public Tester httpPost(String url, String content) throws URISyntaxException, UnsupportedEncodingException {
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

	public Tester httpPost(String url, Object o) throws JsonProcessingException, UnsupportedEncodingException, URISyntaxException {
		return httpPost(url, objectMapper.writeValueAsString(o));
	}

	public Tester httpPut(String url, String content) throws URISyntaxException, UnsupportedEncodingException {
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

	public Tester httpPut(String url, Object o) throws URISyntaxException, UnsupportedEncodingException, JsonProcessingException {
		return httpPut(url, objectMapper.writeValueAsString(o));
	}

	public Tester httpDelete(String url) throws URISyntaxException, UnsupportedEncodingException {
		setUri(url);
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

	public Tester basicAuth(String username, String password) {
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

	public Tester send() throws IOException {
		if (response != null) {
			response.close();
		}
		response = client.execute(request);
		httpEntity = response.getEntity();
		mediaType = MediaType.parse(httpEntity.getContentType().getValue());
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		httpEntity.writeTo(stream);
		content = new String(stream.toByteArray());
		return this;
	}

	/*
	Validation
	 */

	public Tester is(HttpStatus httpStatus) {
		StatusLine statusLine = response.getStatusLine();
		Assert.assertEquals(statusLine.getStatusCode(), httpStatus.value(), "Status: " + statusLine.getReasonPhrase() + "\nMessage: " + content);
		return this;
	}

	public Tester isOk() {
		return is(HttpStatus.OK);
	}

	public Tester isNotFound() {
		return is(HttpStatus.NOT_FOUND);
	}

	public Tester isBadRequest() {
		return is(HttpStatus.BAD_REQUEST);
	}

	public Tester isCreated() {
		return is(HttpStatus.CREATED);
	}

	public Tester matchContentType(MediaType mediaType) {
		assert this.mediaType.type().equals(mediaType.type());
		assert this.mediaType.subtype().equals(mediaType.subtype());
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
	 * Use this to get content in class with generic type.
	 */
	public <T> T getContent(TypeReference<T> typeReference) throws IOException {
		return objectMapper.readValue(content, typeReference);
	}

	/*
	Others
	 */

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
