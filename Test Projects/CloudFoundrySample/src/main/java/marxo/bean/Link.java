package marxo.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.github.jmkgreen.morphia.annotations.Entity;
import org.bson.types.ObjectId;

import java.util.List;

@Entity(value = "links")
@JsonPropertyOrder({"id", "name", "title", "desc", "tenantId", "workflowId", "prevNode", "nextNode", "condition", "type", "status", "created", "createdBy", "modified", "modifiedBy", "objectType"})
public class Link extends BasicEntity {

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

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

	String name, title;
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
	// TODO: embedded condition, hide it for now

	@JsonProperty("prevNode")
	public Node getPreviousNode() {
		return (previousNodeId == null) ? null : new Node() {{
			id = previousNodeId;
		}};
	}

	@JsonProperty("prevNode")
	public void setPreviousNode(Node previousNode) {
		this.previousNodeId = (previousNode == null) ? null : previousNode.id;
	}

	public Node getNextNode() {
		return (nextNodeId == null) ? null : new Node() {{
			id = nextNodeId;
		}};
	}

	public void setNextNode(Node nextNode) {
		this.nextNodeId = (nextNode == null) ? null : nextNode.id;
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
