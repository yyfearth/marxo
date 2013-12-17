package marxo.entity.action;

import marxo.entity.node.Node;
import marxo.entity.workflow.RunStatus;
import marxo.entity.workflow.Workflow;

public class WaitAction extends Action {

	public WaitAction() {
		type = Type.WAIT;
	}

	@Override
	public boolean act(Workflow workflow, Node node) {
		setStatus(RunStatus.FINISHED);
		return true;
	}
}
