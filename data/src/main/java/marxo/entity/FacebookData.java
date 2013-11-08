package marxo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Date;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({"service", "status", "user_id", "username", "fullname", "access_token", "expires_at", "link", "locale"})
public class FacebookData {
	@JsonIgnore
	public String service = "facebook";
	@JsonIgnore
	public FacebookStatus status;
	@JsonProperty("user_id")
	public String userId;
	public String username;
	@JsonProperty("fullname")
	public String fullName;
	@JsonIgnore
	public String accessToken;
	@JsonProperty("expires_at")
	public Date expireDate;
	public String link;
	public String locale;

	@JsonProperty("status")
	public FacebookStatus getStatus() {
		return status;
	}

	@JsonProperty("access_token")
	public void setJsonAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	@JsonProperty("service")
	public String getJsonService() {
		return service;
	}
}
