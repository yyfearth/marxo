package marxo.entity.action;

import com.fasterxml.jackson.annotation.JsonIgnore;
import marxo.entity.node.NodeChildEntity;
import marxo.entity.workflow.RunStatus;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Transient;

public abstract class ActionChildEntity extends NodeChildEntity {
	@Override
	public RunStatus getStatus() {
		if (getAction() == null) {
			return null;
		}
		return getAction().getStatus();
	}

	@Override
	public void setStatus(RunStatus status) {
	}

	public ObjectId actionId;

	@Transient
	@JsonIgnore
	protected Action action;

	@JsonIgnore
	public Action getAction() {
		if (actionId == null) {
			return action = null;
		}
		return (action == null) ? (action = mongoTemplate.findById(actionId, Action.class)) : action;
	}

	@JsonIgnore
	public void setAction(Action action) {
		this.action = action;
		this.actionId = action.id;
		this.nodeId = action.nodeId;
		this.tenantId = action.tenantId;
		this.workflowId = action.workflowId;
	}
}
