package marxo.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Tenant extends Entity {
	public String name = "";
	//    ArrayList<User> users = new ArrayList<>();
	@JsonProperty("desc")
	public String description;
	public String contact;
	public String email;
	@JsonProperty("tel")
	public String phoneNumber;
	public String fax;
	public String address;
}
