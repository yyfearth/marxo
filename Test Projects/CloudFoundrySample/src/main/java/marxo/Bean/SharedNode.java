package marxo.Bean;

import com.github.jmkgreen.morphia.annotations.Entity;

import java.util.UUID;

@Entity(value = "nodes")
public class SharedNode extends BasicEntity {
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public UUID getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(UUID workflowId) {
		this.workflowId = workflowId;
	}

	String name;
	UUID workflowId;
//	List<SharedAction> sharedActions;
}
