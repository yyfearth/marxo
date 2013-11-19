package marxo.controller;

import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import marxo.dao.*;
import marxo.entity.link.Link;
import marxo.entity.node.Node;
import marxo.entity.workflow.Workflow;
import marxo.entity.workflow.WorkflowChildEntity;
import marxo.entity.workflow.WorkflowPredicate;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("ALL")
@Controller
@RequestMapping("workflow{:s?}")
public class WorkflowController extends TenantChildController<Workflow> {
	@Autowired
	NodeController nodeController;
	@Autowired
	LinkController linkController;
	@Autowired
	NodeDao nodeDao;
	@Autowired
	LinkDao linkDao;

	@Autowired
	protected WorkflowController(WorkflowDao dao) {
		super(dao);
	}

	@Override
	public void preHandle() {
		super.preHandle();
		daoContext = daoContext.addContext("isProject", false);
	}

	@Override
	public Workflow read(@PathVariable String idString) throws Exception {
		Workflow workflow = super.read(idString);

		DaoContext daoContext = DaoContext.newInstance().addContext(
				new DaoContextData("workflowId", workflow.id),
				new DaoContextData("tenantId", user.tenantId)
		);
		List<Node> nodes = nodeDao.find(daoContext);
		List<Link> links = linkDao.find(daoContext);
		WorkflowPredicate<WorkflowChildEntity> workflowPredicate = new WorkflowPredicate<>(workflow.id);
		Iterable<Node> workflowNodes = Iterables.filter(nodes, workflowPredicate);
		workflow.nodes = Lists.newArrayList(workflowNodes);
		Iterable<Link> workflowLinks = Iterables.filter(links, workflowPredicate);
		workflow.links = Lists.newArrayList(workflowLinks);

		return workflow;
	}

	/*
	Search
	 */

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	// todo: implemented created and modified search APIs
	public List<Workflow> search() {
		String name = request.getParameter("name");
		boolean hasName = !Strings.isNullOrEmpty(name);

		List<Workflow> workflows;

		if (hasName) {
			workflows = dao.findByName(name, daoContext);
		} else {
			workflows = dao.find(daoContext);
		}

		Collections.sort(workflows, ModifiedDateComparator.SINGLETON);
		return workflows;
	}

	/*
	Sub-resources
	 */

	protected DaoContext getEntityContext(ObjectId workflowId) {
		DaoContext daoContext = DaoContext.newInstance().addContext(
				new DaoContextData("workflowId", workflowId),
				new DaoContextData("tenantId", user.tenantId)
		);
		return daoContext;
	}

	/*
	Node
	 */

	@RequestMapping(value = "/{workflowIdString:[\\da-fA-F]{24}}/node{:s?}", method = RequestMethod.GET)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public List<Node> readAllNodes(@PathVariable String workflowIdString) throws Exception {
		ObjectId workflowId = new ObjectId(workflowIdString);
		return nodeDao.find(getEntityContext(workflowId));
	}

	@RequestMapping(value = "/{workflowIdString:[\\da-fA-F]{24}}/node{:s?}", method = RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(HttpStatus.CREATED)
	public Node createNode(@PathVariable String workflowIdString, @Valid @RequestBody Node node, HttpServletResponse response) throws Exception {
		Assert.isTrue(node.workflowId.equals(new ObjectId(workflowIdString)));

		ObjectId workflowId = new ObjectId(workflowIdString);
		nodeController.daoContext = getEntityContext(workflowId);
		node = nodeController.create(node, response);

		return node;
	}

	@RequestMapping(value = "/{workflowIdString:[\\da-fA-F]{24}}/node{:s?}/{nodeIdString:[\\da-fA-F]{24}}", method = RequestMethod.GET)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public Node readNode(@PathVariable String workflowIdString, @PathVariable String nodeIdString) throws Exception {
		ObjectId workflowId = new ObjectId(workflowIdString);
		nodeController.daoContext = getEntityContext(workflowId);
		Node node = nodeController.read(nodeIdString);
		Assert.isTrue(node.workflowId.equals(new ObjectId(workflowIdString)));

		return node;
	}

	@RequestMapping(value = "/{workflowIdString:[\\da-fA-F]{24}}/node{:s?}/{nodeIdString:[\\da-fA-F]{24}}", method = RequestMethod.PUT)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public Node updateNode(@PathVariable String workflowIdString, @PathVariable String nodeIdString, @Valid @RequestBody Node node) throws Exception {
		Assert.isTrue(node.workflowId.equals(new ObjectId(workflowIdString)));

		ObjectId workflowId = new ObjectId(workflowIdString);
		nodeController.daoContext = getEntityContext(workflowId);
		node = nodeController.update(nodeIdString, node);

		return node;
	}

	@RequestMapping(value = "/{workflowIdString:[\\da-fA-F]{24}}/node{:s?}/{nodeIdString:[\\da-fA-F]{24}}", method = RequestMethod.DELETE)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public Node deleteNode(@PathVariable String workflowIdString, @PathVariable String nodeIdString) throws Exception {
		ObjectId workflowId = new ObjectId(workflowIdString);
		nodeController.daoContext = getEntityContext(workflowId);
		return nodeController.delete(nodeIdString);
	}

	/*
	Link
	 */

	@RequestMapping(value = "/{workflowIdString:[\\da-fA-F]{24}}/link{:s?}", method = RequestMethod.GET)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public List<Link> readAllLinks(@PathVariable String workflowIdString) throws Exception {
		ObjectId workflowId = new ObjectId(workflowIdString);
		return linkDao.find(getEntityContext(workflowId));
	}

	@RequestMapping(value = "/{workflowIdString:[\\da-fA-F]{24}}/link{:s?}", method = RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(HttpStatus.CREATED)
	public Link createLink(@PathVariable String workflowIdString, @Valid @RequestBody Link link, HttpServletResponse response) throws Exception {
		Assert.isTrue(link.workflowId.equals(new ObjectId(workflowIdString)));

		ObjectId workflowId = new ObjectId(workflowIdString);
		linkController.daoContext = getEntityContext(workflowId);
		link = linkController.create(link, response);

		return link;
	}

	@RequestMapping(value = "/{workflowIdString:[\\da-fA-F]{24}}/link{:s?}/{linkIdString:[\\da-fA-F]{24}}", method = RequestMethod.GET)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public Link readLink(@PathVariable String workflowIdString, @PathVariable String linkIdString) throws Exception {
		ObjectId workflowId = new ObjectId(workflowIdString);
		linkController.daoContext = getEntityContext(workflowId);
		Link link = linkController.read(linkIdString);
		Assert.isTrue(link.workflowId.equals(new ObjectId(workflowIdString)));

		return link;
	}

	@RequestMapping(value = "/{workflowIdString:[\\da-fA-F]{24}}/link{:s?}/{linkIdString:[\\da-fA-F]{24}}", method = RequestMethod.PUT)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public Link updateLink(@PathVariable String workflowIdString, @PathVariable String linkIdString, @Valid @RequestBody Link link) throws Exception {
		Assert.isTrue(link.workflowId.equals(new ObjectId(workflowIdString)));

		ObjectId workflowId = new ObjectId(workflowIdString);
		linkController.daoContext = getEntityContext(workflowId);
		link = linkController.update(linkIdString, link);

		return link;
	}

	@RequestMapping(value = "/{workflowIdString:[\\da-fA-F]{24}}/link{:s?}/{linkIdString:[\\da-fA-F]{24}}", method = RequestMethod.DELETE)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public Link deleteLink(@PathVariable String workflowIdString, @PathVariable String linkIdString) throws Exception {
		ObjectId workflowId = new ObjectId(workflowIdString);
		linkController.daoContext = getEntityContext(workflowId);
		return linkController.delete(linkIdString);
	}
}