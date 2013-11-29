package marxo.entity.action;

import marxo.entity.node.Action;
import marxo.entity.node.NodeChildEntity;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Transient;

public abstract class ActionChildEntity extends NodeChildEntity {
	/*
	Action
	 */

	public ObjectId actionId;

	@Transient
	protected Action action;

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
		this.actionId = action.id;
		this.nodeId = action.nodeId;
		this.tenantId = action.tenantId;
	}
}
