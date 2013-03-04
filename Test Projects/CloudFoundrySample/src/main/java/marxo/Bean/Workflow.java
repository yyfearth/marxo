package marxo.Bean;

import com.mongodb.ReflectionDBObject;

import java.util.UUID;

public class Workflow extends ReflectionDBObject {
	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		if (id == null) {
			return;
		} else {
			this.id = id;
		}
	}

	UUID id = UUID.randomUUID();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if (name == null) {
			this.name = "";
		} else {
			this.name = name;
		}
	}

	String name = "";

	public boolean getIsMocked() {
		return isMocked;
	}

	public void setIsMocked(boolean mocked) {
		isMocked = mocked;
	}

	boolean isMocked = false;

	public Workflow() {
	}

	public Workflow(String name) {
		this();
		this.name = name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Workflow)) return false;

		Workflow workflow = (Workflow) o;

		if (isMocked != workflow.isMocked) return false;
		if (!id.equals(workflow.id)) return false;
		if (!name.equals(workflow.name)) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = id.hashCode();
		result = 31 * result + name.hashCode();
		result = 31 * result + (isMocked ? 1 : 0);
		return result;
	}
}
