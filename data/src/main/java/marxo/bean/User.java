package marxo.bean;

import org.bson.types.ObjectId;

public class User extends Entity {
	String name = "";
	ObjectId tenantId = null;

	public User(ObjectId tenantId, String name) {
		this.tenantId = tenantId;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ObjectId getTenantId() {
		return tenantId;
	}

	public void setTenantId(ObjectId tenantId) {
		this.tenantId = tenantId;
	}
}
