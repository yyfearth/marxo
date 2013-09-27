package marxo.bean;

import java.util.ArrayList;

public class Tenant extends Entity {
	String name = "";
	ArrayList<User> users = new ArrayList<User>();

	public Tenant() {

	}

	public ArrayList<User> getUsers() {
		return users;
	}

	public void setUsers(ArrayList<User> users) {
		this.users = users;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
