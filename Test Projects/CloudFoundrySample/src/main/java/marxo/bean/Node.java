package marxo.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.jmkgreen.morphia.annotations.Entity;
import marxo.tool.TypeTool;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

@Entity(value = "nodes")
public class Node extends BasicEntity<Node> {
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

	public List<ObjectId> getActionIds() {
		return actionIds;
	}

	public void setActionIds(List<ObjectId> actionIds) {
		this.actionIds = actionIds;
	}

	String name;
	@JsonIgnore
	ObjectId workflowId;
	@JsonIgnore
	List<ObjectId> actionIds;

	@JsonProperty("workflow")
	public Workflow getWorkflow() {
		return (workflowId == null) ? null : new Workflow() {{
			id = workflowId;
		}};
	}

	@JsonProperty("workflow")
	public void setWorkflow(Workflow workflow) {
		workflowId = (workflow == null) ? null : workflow.id;
	}

	@JsonProperty("actions")
	public Action[] getActions() {
		return TypeTool.toEntities(Action.class, actionIds);
	}

	@JsonProperty("actions")
	public void setActions(Action[] actions) {
		this.actionIds = (actions == null) ? new ArrayList<ObjectId>(0) : TypeTool.toIdList(actions);
	}

}
