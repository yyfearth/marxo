package marxo.validation;

import com.google.common.collect.Maps;
import marxo.entity.link.Link;
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

public class WorkflowValidator extends BasicValidator {
	static protected MongoTemplate mongoTemplate = (MongoTemplate) new ClassPathXmlApplicationContext("classpath*:mongo-configuration.xml").getBean("mongoTemplate");

	/**
	 * Wire dual-directional link and fill up logical fields.
	 *
	 * @param workflow
	 */
	public static void wire(Workflow workflow) {
		Query query = Query.query(Criteria.where("workflowId").is(workflow.id));
		List<Link> links = mongoTemplate.find(query, Link.class);
		List<Node> nodes = mongoTemplate.find(query, Node.class);

		SelectIdFunction selectIdFunction = new SelectIdFunction();
		Map<ObjectId, Node> nodeMap = Maps.uniqueIndex(nodes, selectIdFunction);
		Map<ObjectId, Link> linkMap = Maps.uniqueIndex(links, selectIdFunction);

		for (Node node : nodes) {

		}

		for (Link link : links) {
			Node node;

			node = nodeMap.get(link.previousNodeId);
			link.setPreviousNode(node);
			node.tolinkIds.add(link.id);
			node.toLinks.add(link);

			node = nodeMap.get(link.nextNodeId);
			link.setNextNode(node);
			node.fromlinkIds.add(link.id);
			node.fromLinks.add(link);
		}
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return Workflow.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		super.validate(target, errors);

		if (!(target instanceof Workflow)) {
			errors.reject("target.is.workflow");
			return;
		}

		Workflow workflow = (Workflow) target;

		if (workflow.isProject) {
			ValidationUtils.rejectIfEmpty(errors, "startNodeId", "value.requires");
		}
	}
}