package marxo.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Publisher extends TenantChildEntity {
	@JsonProperty("first_name")
	public String firstName;
	@JsonProperty("last_name")
	public String lastName;
	public String email;
	// review: need to deal with the password security.
	String password;
}
