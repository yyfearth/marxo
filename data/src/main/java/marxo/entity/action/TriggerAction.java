package marxo.entity.action;

import marxo.entity.node.Node;
import marxo.entity.workflow.Notification;
import marxo.entity.workflow.RunStatus;
import marxo.entity.workflow.Workflow;

public class TriggerAction extends Action {

	public TriggerAction() {
		type = Type.TRIGGER;
	}

	@Override
	public boolean act(Workflow workflow, Node node) {
		Notification.saveNew(Notification.Level.MAJOR, this, Notification.Type.WAIT_FOR_USER);
		setStatus(RunStatus.STARTED);
		return false;
	}
}
