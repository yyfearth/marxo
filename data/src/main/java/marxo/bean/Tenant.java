package marxo.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Tenant extends Entity {
	//    ArrayList<User> users = new ArrayList<>();
	@JsonProperty("desc")
	public String description;
	public String contact;
	public String email;
	@JsonProperty("tel")
	public String phoneNumber;
	public String fax;
	public String address;

	@Override
	public void fillWithDefaultValues() {
		super.fillWithDefaultValues();

		if (description == null) {
			description = "";
		}
	}
}
