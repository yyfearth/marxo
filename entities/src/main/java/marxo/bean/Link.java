package marxo.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
@JsonPropertyOrder({"id", "name", "title", "desc", "tenantId", "workflowId", "prevNodeId", "nextNodeId", "condition", "type", "status", "created", "createdBy", "modified", "modifiedBy", "objectType"})
public class Link extends BasicEntity {

	@JsonProperty("desc")
	String description;
	@JsonIgnore
	ObjectId workflowId;
	@JsonIgnore
	List<ObjectId> actionIds;
	@JsonIgnore
	ObjectId previousNodeId;
	@JsonIgnore
	ObjectId nextNodeId;
	@JsonIgnore
	Condition condition;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ObjectId getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(ObjectId workflowId) {
		this.workflowId = workflowId;
	}

	public List<ObjectId> getActionIds() {
		return actionIds;
	}

	public void setActionIds(List<ObjectId> actionIds) {
		this.actionIds = actionIds;
	}

	public ObjectId getPreviousNodeId() {
		return previousNodeId;
	}

	public void setPreviousNodeId(ObjectId previousNodeId) {
		this.previousNodeId = previousNodeId;
	}

	public ObjectId getNextNodeId() {
		return nextNodeId;
	}

	public void setNextNodeId(ObjectId nextNodeId) {
		this.nextNodeId = nextNodeId;
	}

	public Condition getCondition() {
		return condition;
	}

	public void setCondition(Condition condition) {
		this.condition = condition;
	}
	// TODO: embedded condition, hide it for now

	@JsonProperty("prevNodeId")
	public String getJsonPreviousNodeId() {
		return previousNodeId == null ? null : previousNodeId.toString();
	}

	@JsonProperty("prevNodeId")
	public void setJsonPreviousNodeId(String previousNodeId) {
		this.previousNodeId = (previousNodeId == null) ? null : new ObjectId(previousNodeId);
	}

	@JsonProperty("nextNodeId")
	public String getJsonNextNodeId() {
		return nextNodeId == null ? null : nextNodeId.toString();
	}

	@JsonProperty("nextNodeId")
	public void setJsonNextNodeId(String nextNodeId) {
		this.nextNodeId = (nextNodeId == null) ? null : new ObjectId(nextNodeId);
	}

	@JsonProperty("workflowId")
	public String getJsonWorkflowId() {
		return workflowId == null ? null : workflowId.toString();
	}

	@JsonProperty("workflowId")
	public void setJsonWorkflowId(String workflowId) {
		this.workflowId = (workflowId == null) ? null : new ObjectId(workflowId);
	}
}
