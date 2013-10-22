package marxo.entity;

public class User extends TenantChildEntity {
	static {

	}

	protected String password;

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {

		this.password = password;
	}
}
