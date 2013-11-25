package marxo.validation;

import com.google.common.collect.Maps;
import marxo.entity.link.Link;
import marxo.entity.node.Action;
import marxo.entity.node.Node;
import marxo.entity.workflow.Workflow;
import org.bson.types.ObjectId;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import java.util.List;
import java.util.Map;

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