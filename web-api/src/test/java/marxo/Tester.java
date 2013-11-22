package marxo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Strings;
import com.google.common.net.MediaType;
import marxo.serialization.MarxoObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
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

	public Tester httpPost(String url, String content) throws URISyntaxException, UnsupportedEncodingException {
		this.uri = new URI(url);

		HttpPost httpPost = new HttpPost(uri);
		if (!Strings.isNullOrEmpty(content)) {
			StringEntity stringEntity = new StringEntity(content);
			httpPost.setEntity(stringEntity);
		}
		httpPost.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.JSON_UTF_8.toString());
		request = httpPost;

		return this;
	}

	public Tester httpPost(String url, Object object) throws JsonProcessingException, UnsupportedEncodingException, URISyntaxException {
		return httpPost(url, objectMapper.writeValueAsString(object));
	}

	public Tester basicAuth(String username, String password) {
		String credentialString = username + ":" + password;
		String credential = DatatypeConverter.printBase64Binary(credentialString.getBytes());
		authorizationHeader = new BasicHeader("Authorization", "Basic " + credential);
		request.addHeader(authorizationHeader);
		return this;
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
