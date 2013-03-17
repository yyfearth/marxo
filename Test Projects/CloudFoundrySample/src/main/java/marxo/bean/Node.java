package marxo.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.github.jmkgreen.morphia.annotations.Entity;
import marxo.tool.TypeTool;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

@Entity(value = "nodes", noClassnameStored = true)
@JsonPropertyOrder({"id", "tenantId", "name", "title", "desc", "type", "status", "actions", "created", "createdBy", "modified", "modifiedBy", "objectType"})
public class Node extends BasicEntity {

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

	String name, title;
	@JsonProperty("desc")
	String description;
	ObjectId workflowId;
	@JsonIgnore
	List<ObjectId> actionIds;

	@JsonProperty("actions")
	public Action[] getActions() {
		return TypeTool.toEntities(Action.class, actionIds);
	}

	@JsonProperty("actions")
	public void setActions(Action[] actions) {
		this.actionIds = (actions == null) ? new ArrayList<ObjectId>(0) : TypeTool.toIdList(actions);
	}

}
