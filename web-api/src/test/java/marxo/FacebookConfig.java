package marxo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FacebookConfig {
	@Bean
	public String appId() {
		return "213527892138380";
	}

	@Bean
	public String appSecret() {
		return "8157179216ad4e065d10c425f382499d";
	}

	@Bean
	public String appToken() {
		return "213527892138380|eZgwp-1kegwB-CI-pYi7Q2WllKw";
	}
}
