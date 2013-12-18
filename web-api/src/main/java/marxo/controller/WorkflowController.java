package marxo.controller;

import com.google.common.base.Strings;
import com.mongodb.WriteResult;
import marxo.entity.Task;
import marxo.entity.action.Action;
import marxo.entity.link.Link;
import marxo.entity.node.Node;
import marxo.entity.workflow.RunStatus;
import marxo.entity.workflow.Workflow;
import marxo.exception.DataInconsistentException;
import marxo.exception.EntityInvalidException;
import marxo.exception.EntityNotFoundException;
import marxo.exception.ValidationException;
import marxo.tool.StringTool;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.regex.Pattern;

@SuppressWarnings("ALL")
@Controller
@RequestMapping("workflow{:s?}")
public class WorkflowController extends TenantChildController<Workflow> {

	@Autowired
	NodeController nodeController;
	@Autowired
	LinkController linkController;

	@Override
	public void preHandle() {
		super.preHandle();
	}

	protected boolean isProject() {
		return false;
	}

	@Override
	protected Criteria newDefaultCriteria() {
		Criteria criteria = super.newDefaultCriteria();
		return criteria.and("isProject").is(isProject());
	}

	@Override
	protected Criteria newDefaultCriteria(ObjectId id) {
		Criteria criteria = super.newDefaultCriteria(id);
		return criteria.and("isProject").is(isProject());
	}

	/*
	CRUD
	 */

	@Override
	public Workflow read(@PathVariable String idString) throws Exception {
		Workflow workflow = super.read(idString);
		workflow.getNodes();
		workflow.getLinks();

		return workflow;
	}

	@Override
	public Workflow update(@Valid @PathVariable String idString, @Valid @RequestBody Workflow workflow) throws Exception {
		ObjectId objectId = stringToObjectId(idString);

		Workflow oldWorkflow = mongoTemplate.findById(objectId, entityClass);
		if (oldWorkflow == null) {
			throw new EntityNotFoundException(entityClass, objectId);
		}

		if (!oldWorkflow.getStatus().equals(workflow.getStatus())) {
			switch (workflow.getStatus()) {
				case STARTED:
					Task task = new Task(workflow.id);
					task.save();
					break;
				case PAUSED:
				case STOPPED:
					mongoTemplate.remove(Query.query(Criteria.where("workflowId").is(workflow.id)), Task.class);
					logger.debug(String.format("%s is %s. Remove all associated tasks", workflow, workflow.getStatus()));
					break;
				case IDLE:
					for (Node node : workflow.getNodes()) {
						for (Action action : node.getActions()) {
							action.setStatus(RunStatus.IDLE);
						}
						node.setStatus(RunStatus.IDLE);
					}
					for (Link link : workflow.getLinks()) {
						link.setStatus(RunStatus.IDLE);
					}
					break;
				case FINISHED:
				case ERROR:
				case WAITING:
				case TRACKED:
				default:
					throw new IllegalArgumentException(String.format("You cannot change a project's status to %s", workflow.getStatus()));
			}
		}

		try {
			workflow.id = oldWorkflow.id;
			workflow.createUserId = oldWorkflow.createUserId;
			workflow.createTime = oldWorkflow.createTime;
			workflow.updateUserId = user.id;
			workflow.updateTime = DateTime.now();
			workflow.save();
		} catch (ValidationException ex) {
			for (int i = 0; i < ex.reasons.size(); i++) {
				logger.error(ex.reasons.get(i));
			}
			throw new EntityInvalidException(objectId, ex.reasons);
		}

		return workflow;
	}

	/*
	Update
	 */

	@RequestMapping(value = "/{idString:[\\da-fA-F]{24}}/status", method = RequestMethod.PUT)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public RunStatus updateStatus(@PathVariable String idString, @RequestBody RunStatus status) throws Exception {
		ObjectId workflowId = stringToObjectId(idString);

		Update update = Update.update("status", status);
		WriteResult result = mongoTemplate.updateFirst(newDefaultQuery(workflowId), update, Workflow.class);
		throwIfError(result);

		if (result.getN() == 0) {
			throw new EntityNotFoundException(Workflow.class, workflowId);
		}

		Workflow workflow = Workflow.get(workflowId);

		if (workflow.is(RunStatus.STARTED)) {
			Task.schedule(workflowId, DateTime.now());

			if (workflow.getNodes().isEmpty()) {
				throw new DataInconsistentException(String.format("%s has no node", workflow));
			}
			if (workflow.getStartNode() == null) {
				workflow.getNodes();
				workflow.getLinks();
				workflow.wire();

				if (!workflow.isValidated()) {
					throw new DataInconsistentException(String.format("%s is not valid", workflow));
				}
			}
			workflow.addCurrentNode(workflow.getStartNode());
			workflow.save();
		} else if (workflow.is(RunStatus.STOPPED)) {
			mongoTemplate.remove(Query.query(Criteria.where("workflowId").is(workflowId)), Task.class);

			workflow.getCurrentNodes().clear();
			workflow.getCurrentLinks().clear();
			for (Node node : workflow.getNodes()) {
				node.setStatus(RunStatus.IDLE);

				for (Action action : node.getActions()) {
					action.setStatus(RunStatus.IDLE);
					if (action.getContent() != null && action.getContent().records != null) {
						action.getContent().records.clear();
					}
				}
			}
			for (Link link : workflow.getLinks()) {
				link.setStatus(RunStatus.IDLE);
			}
		} else if (workflow.is(RunStatus.PAUSED)) {
			mongoTemplate.remove(Query.query(Criteria.where("workflowId").is(workflowId)), Task.class);
		}

		return status;
	}

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public List<Workflow> search() {
		String name = request.getParameter("name");
		boolean hasName = !Strings.isNullOrEmpty(name);

		List<Workflow> workflows;

		Criteria criteria = newDefaultCriteria();
		if (hasName) {
			String escapedName = StringTool.escapePatternCharacters(name);
			Pattern pattern = Pattern.compile(".*" + escapedName + ".*", Pattern.CASE_INSENSITIVE);
			criteria.and("name").regex(pattern);
		}

		workflows = mongoTemplate.find(new Query(criteria).with(getDefaultSort()), entityClass);

		return workflows;
	}

	/*
	Sub-resources
	 */

	private void setSubResourceCriteria(Criteria criteria) {
		Criteria criteria1 = Criteria.where("tenantId").is(user.tenantId);
		Criteria criteria2 = Criteria.where("tenantId").exists(false);
		criteria.orOperator(criteria1, criteria2);
	}

	/*
	Node
	 */

	@RequestMapping(value = "/{workflowIdString:[\\da-fA-F]{24}}/node{:s?}", method = RequestMethod.GET)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public List<Node> readAllNodes(@PathVariable String workflowIdString) throws Exception {
		Workflow workflow = super.read(workflowIdString);
		return workflow.getNodes();
	}

//	@RequestMapping(value = "/{workflowIdString:[\\da-fA-F]{24}}/node{:s?}", method = RequestMethod.POST)
//	@ResponseBody
//	@ResponseStatus(HttpStatus.CREATED)
//	public Node createNode(@PathVariable String workflowIdString, @Valid @RequestBody Node node, HttpServletResponse response) throws Exception {
//		Assert.isTrue(node.workflowId.equals(new ObjectId(workflowIdString)));
//
//		ObjectId workflowId = new ObjectId(workflowIdString);
//
//		Criteria criteria = Criteria.where("workflowId").is(workflowId);
//		setSubResourceCriteria(criteria);
//		nodeController.criteria = criteria;
//
//		return nodeController.create(node, response);
//	}

	@RequestMapping(value = "/{workflowIdString:[\\da-fA-F]{24}}/node{:s?}/{nodeIdString:[\\da-fA-F]{24}}", method = RequestMethod.GET)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public Node readNode(@PathVariable String workflowIdString, @PathVariable String nodeIdString) throws Exception {
		ObjectId workflowId = stringToObjectId(workflowIdString);
		ObjectId nodeId = stringToObjectId(nodeIdString);

		Node node = mongoTemplate.findOne(new Query(Criteria.where("_id").is(nodeId).and("workflowId").is(workflowId)), Node.class);
		if (node == null) {
			throw new EntityNotFoundException(Node.class, nodeId);
		}

		return node;
	}

//	@RequestMapping(value = "/{workflowIdString:[\\da-fA-F]{24}}/node{:s?}/{nodeIdString:[\\da-fA-F]{24}}", method = RequestMethod.PUT)
//	@ResponseBody
//	@ResponseStatus(HttpStatus.OK)
//	public Node updateNode(@PathVariable String workflowIdString, @PathVariable String nodeIdString, @Valid @RequestBody Node node) throws Exception {
//		Assert.isTrue(node.workflowId.equals(new ObjectId(workflowIdString)));
//
//		ObjectId workflowId = new ObjectId(workflowIdString);
//
//		Criteria criteria = Criteria.where("workflowId").is(workflowId);
//		setSubResourceCriteria(criteria);
//		nodeController.criteria = criteria;
//
//		return nodeController.update(nodeIdString, node);
//	}

//	@RequestMapping(value = "/{workflowIdString:[\\da-fA-F]{24}}/node{:s?}/{nodeIdString:[\\da-fA-F]{24}}", method = RequestMethod.DELETE)
//	@ResponseBody
//	@ResponseStatus(HttpStatus.OK)
//	public Node deleteNode(@PathVariable String workflowIdString, @PathVariable String nodeIdString) throws Exception {
//		ObjectId workflowId = new ObjectId(workflowIdString);
//
//		Criteria criteria = Criteria.where("workflowId").is(workflowId);
//		setSubResourceCriteria(criteria);
//		nodeController.criteria = criteria;
//
//		return nodeController.delete(nodeIdString);
//	}

	/*
	Link
	 */

	@RequestMapping(value = "/{workflowIdString:[\\da-fA-F]{24}}/link{:s?}", method = RequestMethod.GET)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public List<Link> readAllLinks(@PathVariable String workflowIdString) throws Exception {
		Workflow workflow = super.read(workflowIdString);
		return workflow.getLinks();
	}

//	@RequestMapping(value = "/{workflowIdString:[\\da-fA-F]{24}}/link{:s?}", method = RequestMethod.POST)
//	@ResponseBody
//	@ResponseStatus(HttpStatus.CREATED)
//	public Link createLink(@PathVariable String workflowIdString, @Valid @RequestBody Link link, HttpServletResponse response) throws Exception {
//		Assert.isTrue(link.workflowId.equals(new ObjectId(workflowIdString)));
//
//		ObjectId workflowId = new ObjectId(workflowIdString);
//
//		Criteria criteria = Criteria.where("workflowId").is(workflowId);
//		setSubResourceCriteria(criteria);
//		linkController.criteria = criteria;
//		link = linkController.create(link, response);
//
//		return link;
//	}

	@RequestMapping(value = "/{workflowIdString:[\\da-fA-F]{24}}/link{:s?}/{linkIdString:[\\da-fA-F]{24}}", method = RequestMethod.GET)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public Link readLink(@PathVariable String workflowIdString, @PathVariable String linkIdString) throws Exception {
		ObjectId workflowId = stringToObjectId(workflowIdString);
		ObjectId linkId = stringToObjectId(linkIdString);

		Link link = mongoTemplate.findOne(new Query(Criteria.where("_id").is(linkId).and("workflowId").is(workflowId)), Link.class);
		if (link == null) {
			throw new EntityNotFoundException(Link.class, linkId);
		}

		return link;
	}

//	@RequestMapping(value = "/{workflowIdString:[\\da-fA-F]{24}}/link{:s?}/{linkIdString:[\\da-fA-F]{24}}", method = RequestMethod.PUT)
//	@ResponseBody
//	@ResponseStatus(HttpStatus.OK)
//	public Link updateLink(@PathVariable String workflowIdString, @PathVariable String linkIdString, @Valid @RequestBody Link link) throws Exception {
//		Assert.isTrue(link.workflowId.equals(new ObjectId(workflowIdString)));
//
//		ObjectId workflowId = new ObjectId(workflowIdString);
//
//		Criteria criteria = Criteria.where("workflowId").is(workflowId);
//		setSubResourceCriteria(criteria);
//		linkController.criteria = criteria;
//
//		return linkController.update(linkIdString, link);
//	}
//
//	@RequestMapping(value = "/{workflowIdString:[\\da-fA-F]{24}}/link{:s?}/{linkIdString:[\\da-fA-F]{24}}", method = RequestMethod.DELETE)
//	@ResponseBody
//	@ResponseStatus(HttpStatus.OK)
//	public Link deleteLink(@PathVariable String workflowIdString, @PathVariable String linkIdString) throws Exception {
//		ObjectId workflowId = new ObjectId(workflowIdString);
//
//		Criteria criteria = Criteria.where("workflowId").is(workflowId);
//		setSubResourceCriteria(criteria);
//		linkController.criteria = criteria;
//
//		return linkController.delete(linkIdString);
//	}
}