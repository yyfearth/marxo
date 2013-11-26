package marxo.validation;

import marxo.entity.node.Action;
import marxo.entity.node.Node;
import org.springframework.validation.Errors;

import java.util.List;

/*
todo:
project:
* the startNodeId should not be null
*/

public class NodeValidator extends BasicValidator {
	public static void wire(Node node) {
		List<Action> actions = node.getActions();
		for (int i = 0, len = actions.size(); i < len; i++) {
			Action action = actions.get(i);
			action.setNode(node);
			if (i + 1 != len) {
				action.setNextAction(actions.get(i + 1));
			}

			action.setNode(node);
			action.tenantId = node.tenantId;
		}
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return Node.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		super.validate(target, errors);

		Node node = (Node) target;
	}
}