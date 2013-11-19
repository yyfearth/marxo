package marxo.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.joda.time.DateTime;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({"service", "status", "user_id", "username", "fullname", "access_token", "expires_at", "link", "locale"})
public class FacebookData {
	public String service = "facebook";
	public FacebookStatus status;
	public String userId;
	public String username;
	public String fullName;
	public String accessToken;
	@JsonProperty("expires_at")
	public DateTime expireDate;
	public String link;
	public String locale;
}
