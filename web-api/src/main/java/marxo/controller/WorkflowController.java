package marxo.controller;

import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import marxo.dao.LinkDao;
import marxo.dao.NodeDao;
import marxo.dao.WorkflowDao;
import marxo.entity.*;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("workflow{:s?}")
public class WorkflowController extends TenantChildController<Workflow> {
	static final ModifiedDateComparator modifiedDateComparator = new ModifiedDateComparator();
	@Autowired
	NodeDao nodeDao;
	@Autowired
	LinkDao linkDao;
	@Autowired
	WorkflowDao workflowDao;
	@Autowired
	NodeController nodeController;
	@Autowired
	LinkController linkController;

	@Autowired
	public WorkflowController(WorkflowDao workflowDao) {
		super(workflowDao);
		this.workflowDao = workflowDao;
	}

	@Override
	public void preHandle() {
		super.preHandle();
		nodeDao.setTenantId(user.tenantId);
		linkDao.setTenantId(user.tenantId);
	}

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public List<Workflow> getAll(@RequestParam(required = false) String name, @RequestParam(required = false) Date modified, @RequestParam(required = false) Date created, @RequestParam(value = "is_project", required = false) boolean isProject) {
		boolean hasName = !Strings.isNullOrEmpty(name);
		boolean hasCreated = created != null;
		boolean hasModified = modified != null;
		List<Workflow> workflows;

//		if (!hasName && !hasCreated && !hasModified) {
//			workflows = tenantChildDao.findAll();
//			ArrayList<ObjectId> workflowIds = new ArrayList<>(workflows.size());
//			for (Workflow workflow : workflows) {
//				workflowIds.add(workflow.id);
//			}
//
//			List<Node> nodes = nodeDao.searchByWorkflowIds(workflowIds);
//			List<Link> links = linkDao.searchByWorkflowIds(workflowIds);
//
//			// Java really needs a kick-ass collection library for the following. I have used Guava for this, it still looks as bad as it could.
//			// The following is for getting all nodes and links which match the workflow's ID.
//			for (Workflow workflow : workflows) {
//				WorkflowPredicate<WorkflowChildEntity> workflowPredicate = new WorkflowPredicate<>(workflow.id);
//				Iterable<Node> workflowNodes = Iterables.filter(nodes, workflowPredicate);
//				workflow.nodes = Lists.newArrayList(workflowNodes);
//				Iterable<Link> workflowLinks = Iterables.filter(links, workflowPredicate);
//				workflow.links = Lists.newArrayList(workflowLinks);
//			}
//
//			Collections.sort(workflows, modifiedDateComparator);
//			return workflows;
//		}

		// todo: implemented created and modified search APIs

		if (hasName) {
			workflows = workflowDao.searchByName(name);
		} else if (isProject) {
			workflows = workflowDao.search("isProject", true);
		} else {
			workflows = workflowDao.findAll();
		}

		Collections.sort(workflows, modifiedDateComparator);
		return workflows;
	}

	@Override
	public Workflow read(@PathVariable String idString) {
		Workflow workflow = super.read(idString);

		List<Node> nodes = nodeDao.searchByWorkflowId(workflow.id);
		List<Link> links = linkDao.searchByWorkflowId(workflow.id);
		WorkflowPredicate<WorkflowChildEntity> workflowPredicate = new WorkflowPredicate<>(workflow.id);
		Iterable<Node> workflowNodes = Iterables.filter(nodes, workflowPredicate);
		workflow.nodes = Lists.newArrayList(workflowNodes);
		Iterable<Link> workflowLinks = Iterables.filter(links, workflowPredicate);
		workflow.links = Lists.newArrayList(workflowLinks);

		return workflow;
	}

	/*
	Sub-resources
	 */

	/*
	Node
	 */

	@RequestMapping(value = "/{workflowIdString:[\\da-fA-F]{24}}/node{:s?}", method = RequestMethod.GET)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public List<Node> readAllNodes(@PathVariable String workflowIdString) throws Exception {
		ObjectId workflowId = new ObjectId(workflowIdString);
		return nodeDao.searchByWorkflowId(workflowId);
	}

	@RequestMapping(value = "/{workflowIdString:[\\da-fA-F]{24}}/node{:s?}", method = RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(HttpStatus.CREATED)
	public Node createNode(@PathVariable String workflowIdString, @Valid @RequestBody Node node, HttpServletResponse response) throws Exception {
		Assert.isTrue(node.workflowId.equals(new ObjectId(workflowIdString)));
		node = nodeController.create(node, response);

		return node;
	}

	@RequestMapping(value = "/{workflowIdString:[\\da-fA-F]{24}}/node{:s?}/{nodeIdString:[\\da-fA-F]{24}}", method = RequestMethod.GET)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public Node readNode(@PathVariable String workflowIdString, @PathVariable String nodeIdString) throws Exception {
		Node node = nodeController.read(nodeIdString);
		Assert.isTrue(node.workflowId.equals(new ObjectId(workflowIdString)));

		return node;
	}

	@RequestMapping(value = "/{workflowIdString:[\\da-fA-F]{24}}/node{:s?}/{nodeIdString:[\\da-fA-F]{24}}", method = RequestMethod.PUT)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public Node updateNode(@PathVariable String workflowIdString, @PathVariable String nodeIdString, @Valid @RequestBody Node node) throws Exception {
		Assert.isTrue(node.workflowId.equals(new ObjectId(workflowIdString)));
		node = nodeController.update(nodeIdString, node);

		return node;
	}

	@RequestMapping(value = "/{workflowIdString:[\\da-fA-F]{24}}/node{:s?}/{nodeIdString:[\\da-fA-F]{24}}", method = RequestMethod.DELETE)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public Node deleteNode(@PathVariable String workflowIdString, @PathVariable String nodeIdString) throws Exception {
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
		return linkDao.searchByWorkflowId(workflowId);
	}

	@RequestMapping(value = "/{workflowIdString:[\\da-fA-F]{24}}/link{:s?}", method = RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(HttpStatus.CREATED)
	public Link createLink(@PathVariable String workflowIdString, @Valid @RequestBody Link link, HttpServletResponse response) throws Exception {
		Assert.isTrue(link.workflowId.equals(new ObjectId(workflowIdString)));
		link = linkController.create(link, response);

		return link;
	}

	@RequestMapping(value = "/{workflowIdString:[\\da-fA-F]{24}}/link{:s?}/{linkIdString:[\\da-fA-F]{24}}", method = RequestMethod.GET)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public Link readLink(@PathVariable String workflowIdString, @PathVariable String linkIdString) throws Exception {
		Link link = linkController.read(linkIdString);
		Assert.isTrue(link.workflowId.equals(new ObjectId(workflowIdString)));

		return link;
	}

	@RequestMapping(value = "/{workflowIdString:[\\da-fA-F]{24}}/link{:s?}/{linkIdString:[\\da-fA-F]{24}}", method = RequestMethod.PUT)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public Link updateLink(@PathVariable String workflowIdString, @PathVariable String linkIdString, @Valid @RequestBody Link link) throws Exception {
		Assert.isTrue(link.workflowId.equals(new ObjectId(workflowIdString)));
		link = linkController.update(linkIdString, link);

		return link;
	}

	@RequestMapping(value = "/{workflowIdString:[\\da-fA-F]{24}}/link{:s?}/{linkIdString:[\\da-fA-F]{24}}", method = RequestMethod.DELETE)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public Link deleteLink(@PathVariable String workflowIdString, @PathVariable String linkIdString) throws Exception {
		return linkController.delete(linkIdString);
	}
}

class ModifiedDateComparator implements Comparator<Workflow> {
	@Override
	public int compare(Workflow w1, Workflow w2) {
		return w1.modifiedDate.compareTo(w2.modifiedDate);
	}
}