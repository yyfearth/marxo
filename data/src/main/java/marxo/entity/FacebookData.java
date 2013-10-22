package marxo.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Date;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({"service", "status", "user_id", "username", "fullname", "access_token", "expires_at", "link", "locale"})
public class FacebookData {
	public final static String service = "facebook";
	public FacebookStatus status = FacebookStatus.Disconntected;
	@JsonProperty("user_id")
	public String userId;
	public String username;
	@JsonProperty("fullname")
	public String fullName;
	@JsonProperty("access_token")
	public String accessToken;
	@JsonProperty("expires_at")
	public Date expireDate;
	public String link;
	public String locale;
}
