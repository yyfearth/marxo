package marxo.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.types.ObjectId;

public abstract class WorkflowChildEntity extends BasicEntity {
	@JsonIgnore
	public ObjectId workflowId;

	@JsonProperty("workflow_id")
	public String getJsonWorkflowId() {
		return workflowId == null ? null : workflowId.toString();
	}

	@JsonProperty("workflow_id")
	public void setJsonWorkflowId(String workflowId) {
		this.workflowId = (workflowId == null) ? null : new ObjectId(workflowId);
	}
}
