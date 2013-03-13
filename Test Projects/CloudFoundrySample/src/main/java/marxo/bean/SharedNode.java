package marxo.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.jmkgreen.morphia.annotations.Entity;
import org.bson.types.ObjectId;

import java.util.List;

@Entity(value = "nodes")
public class SharedNode extends BasicEntity {
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ObjectId getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(ObjectId workflowId) {
		this.workflowId = workflowId;
	}

	String name;
	@JsonIgnore
	ObjectId workflowId;
	List<SharedAction> sharedActions;

	@JsonProperty("workflowId")
	public String getJsonWorkflowId() {
		return (workflowId == null) ? null : workflowId.toString();
	}

	@JsonProperty("workflowId")
	public void setJsonWorkflowId(String workflowId) {
		this.workflowId = (workflowId == null) ? null : new ObjectId(workflowId);
	}
}
