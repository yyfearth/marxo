package marxo.entity.action;

import marxo.entity.node.Node;
import marxo.entity.workflow.Workflow;

public class TwitterAction extends TrackableAction {

	public TwitterAction() {
		type = Type.TWITTER;
	}

	@Override
	public boolean act(Workflow workflow, Node node) {
		return true;
	}
}
