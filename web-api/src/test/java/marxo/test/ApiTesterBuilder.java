package marxo.test;

import java.net.URISyntaxException;

public class ApiTesterBuilder {
	String baseUrl;
	String username;
	String password;

	public ApiTester build() throws URISyntaxException {
		ApiTester apiTester = new ApiTester();

		if (baseUrl != null) {
			apiTester.baseUrl(baseUrl);
		}

		if (username != null && password != null) {
			apiTester.basicAuth(username, password);
		}

		return apiTester;
	}

	public ApiTesterBuilder baseUrl(String url) throws URISyntaxException {
		this.baseUrl = url;
		return this;
	}

	public ApiTesterBuilder basicAuth(String username, String password) {
		this.username = username;
		this.password = password;
		return this;
	}
}
