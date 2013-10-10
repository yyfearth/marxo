package marxo.controller;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.sun.istack.internal.Nullable;
import marxo.bean.Link;
import marxo.bean.Node;
import marxo.bean.Workflow;
import marxo.bean.WorkflowChildEntity;
import marxo.dao.LinkDao;
import marxo.dao.NodeDao;
import marxo.dao.WorkflowDao;
import marxo.exception.InvalidObjectIdException;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("workflow{:s?}")
public class WorkflowController extends GenericController<Workflow, WorkflowDao> {
	@Autowired
	NodeDao nodeDao;
	@Autowired
	LinkDao linkDao;

	@Autowired
	public WorkflowController(WorkflowDao dao) {
		super(dao);
	}

	@Override
	public List<Workflow> getAll() {
		List<Workflow> workflows = dao.findAll();
		ArrayList<ObjectId> workflowIds = new ArrayList<>(workflows.size());
		for (Workflow workflow : workflows) {
			workflowIds.add(workflow.id);
		}

		List<Node> nodes = nodeDao.searchByWorkflows(workflowIds);
		List<Link> links = linkDao.searchByWorkflows(workflowIds);

		// Java really needs a kick-ass collection library for the following.
		for (Workflow workflow : workflows) {
			WorkflowPredicate<WorkflowChildEntity> workflowPredicate = new WorkflowPredicate<>(workflow.id);
			Iterable<Node> workflowNodes = Iterables.filter(nodes, workflowPredicate);
			workflow.nodes = Lists.newArrayList(workflowNodes);
			Iterable<Link> workflowLinks = Iterables.filter(links, workflowPredicate);
			workflow.links = Lists.newArrayList(workflowLinks);
		}

		return workflows;
	}

	@Override
	public Workflow read(@PathVariable String id) {
		Workflow workflow = super.read(id);

		List<Node> nodes = nodeDao.searchByWorkflow(workflow.id);
		List<Link> links = linkDao.searchByWorkflow(workflow.id);
		WorkflowPredicate<WorkflowChildEntity> workflowPredicate = new WorkflowPredicate<>(workflow.id);
		Iterable<Node> workflowNodes = Iterables.filter(nodes, workflowPredicate);
		workflow.nodes = Lists.newArrayList(workflowNodes);
		Iterable<Link> workflowLinks = Iterables.filter(links, workflowPredicate);
		workflow.links = Lists.newArrayList(workflowLinks);

		return workflow;
	}

	/**
	 * Why use hyphens rather then underscores? See https://support.google.com/webmasters/answer/76329?hl=en
	 */
	@RequestMapping(value = "/search", method = RequestMethod.GET)
	@ResponseBody
	public List<Workflow> search(@RequestParam(value = "tenant-id", required = false) String tenantId, @RequestParam(required = false) String name) {
		// todo: add more parameters if required and verify them.
//		if (!ObjectId.isValid(tenantId)) {
//			throw new InvalidObjectIdException(tenantId, "Tenant ID is not valid.");
//		}

		return dao.searchByName(name);
	}
}

class WorkflowPredicate<E extends WorkflowChildEntity> implements Predicate<E> {
	ObjectId workflowId;

	WorkflowPredicate(ObjectId workflowId) {
		this.workflowId = workflowId;
	}

	@Override
	public boolean apply(@Nullable E entity) {
		return (entity != null) && workflowId.equals(entity.workflowId);
	}
}