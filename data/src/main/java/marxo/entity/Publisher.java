package marxo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.types.ObjectId;

public class Publisher extends BasicEntity {
	@JsonProperty("first_name")
	public String firstName;
	@JsonProperty("last_name")
	public String lastName;
	public String email;
	// review: need to deal with the password security.
	String password;
	@JsonIgnore
	String tenantId;

	@JsonProperty("tenant_id")
	public String getTenantId() {
		return (id == null) ? null : id.toString();
	}

	@JsonProperty("tenant_id")
	public void setTenantId(String id) {
		this.id = new ObjectId(id);
	}
}
