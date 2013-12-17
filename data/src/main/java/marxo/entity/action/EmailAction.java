package marxo.entity.action;

import marxo.entity.node.Node;
import marxo.entity.workflow.Workflow;

public class EmailAction extends Action {

	public EmailAction() {
		type = Type.EMAIL;
	}

	@Override
	public boolean act(Workflow workflow, Node node) {
		return true;
	}
}
