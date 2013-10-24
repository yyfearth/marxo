package marxo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Tenant extends BasicEntity {
	public String contact;
	public String email;
	@JsonProperty("tel")
	public String phoneNumber;
	public String fax;
	public String address;
	@JsonIgnore
	FacebookData facebookData;

	@JsonProperty("facebook-data")
	public FacebookData getFacebookData() {
		return facebookData;
	}

	@JsonProperty("facebook-data")
	public void setFacebookData(FacebookData facebookData) {
		this.facebookData = facebookData;
	}
}
