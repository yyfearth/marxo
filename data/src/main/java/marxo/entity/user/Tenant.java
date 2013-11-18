package marxo.entity.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import marxo.entity.BasicEntity;
import marxo.entity.FacebookData;

public class Tenant extends BasicEntity {
	public String contact;
	public String email;
	@JsonProperty("tel")
	public String phoneNumber;
	public String fax;
	public String address;
	public FacebookData facebookData;
}
