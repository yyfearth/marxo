package marxo.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Publisher extends TenantChildEntity {
	public String firstName;
	public String lastName;
	public String email;
	// review: need to deal with the password security.
	String password;
}
