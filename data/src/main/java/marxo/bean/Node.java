package marxo.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import marxo.tool.TypeTool;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document
public class Node extends BasicEntity {

	public Position positoin;
	@JsonIgnore
	public ObjectId workflowId;
	public List<Action> actions;

	public Node() {
	}

	public Node(ObjectId workflowId) {
		this.workflowId = workflowId;
	}

//	@JsonProperty("actions")
//	public Action[] getActions() {
//		return TypeTool.toEntities(Action.class, actionIds);
//	}
//
//	@JsonProperty("actions")
//	public void setActions(Action[] actions) {
//		this.actionIds = (actions == null) ? new ArrayList<ObjectId>(0) : TypeTool.toIdList(actions);
//	}

	@JsonProperty("workflow_id")
	public String getJsonWorkflowId() {
		return workflowId == null ? null : workflowId.toString();
	}

	@JsonProperty("workflow_id")
	public void setJsonWorkflowId(String workflowId) {
		this.workflowId = (workflowId == null) ? null : new ObjectId(workflowId);
	}

	class Position {
		public double x;
		public double y;
	}

	@Override
	public void fillWithDefaultValues() {
		super.fillWithDefaultValues();

		if (actions == null) {
			actions = new ArrayList<>();
		}
	}
}
