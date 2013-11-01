package marxo.controller;

import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import marxo.dao.LinkDao;
import marxo.dao.NodeDao;
import marxo.dao.WorkflowDao;
import marxo.entity.*;
import marxo.security.MarxoAuthentication;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Controller
@RequestMapping("workflow{:s?}")
public class WorkflowController extends EntityController<Workflow, WorkflowDao> {
	static final ModifiedDateComparator modifiedDateComparator = new ModifiedDateComparator();
	@Autowired
	NodeDao nodeDao;
	@Autowired
	LinkDao linkDao;

	@Autowired
	public WorkflowController(WorkflowDao workflowDao) {
		super(workflowDao);
	}

	@Override
	public List<Workflow> getAll(@RequestParam(required = false) String name, @RequestParam(required = false) Date modified, @RequestParam(required = false) Date created) {
		MarxoAuthentication marxoAuthentication = (MarxoAuthentication) SecurityContextHolder.getContext().getAuthentication();
		User user = marxoAuthentication.getUser();
		dao.setTenantId(user.tenantId);

		boolean hasName = !Strings.isNullOrEmpty(name);
		boolean hasCreated = created != null;
		boolean hasModified = modified != null;
		List<Workflow> workflows;

		if (!hasName && !hasCreated && !hasModified) {
			workflows = dao.findAll();
			ArrayList<ObjectId> workflowIds = new ArrayList<>(workflows.size());
			for (Workflow workflow : workflows) {
				workflowIds.add(workflow.id);
			}

			List<Node> nodes = nodeDao.searchByWorkflowIds(workflowIds);
			List<Link> links = linkDao.searchByWorkflowIds(workflowIds);

			// Java really needs a kick-ass collection library for the following. I have used Guava for this, it still looks as bad as it could.
			// The following is for getting all nodes and links which match the workflow's ID.
			for (Workflow workflow : workflows) {
				WorkflowPredicate<WorkflowChildEntity> workflowPredicate = new WorkflowPredicate<>(workflow.id);
				Iterable<Node> workflowNodes = Iterables.filter(nodes, workflowPredicate);
				workflow.nodes = Lists.newArrayList(workflowNodes);
				Iterable<Link> workflowLinks = Iterables.filter(links, workflowPredicate);
				workflow.links = Lists.newArrayList(workflowLinks);
			}

			Collections.sort(workflows, modifiedDateComparator);
			return workflows;
		}

		if (hasName) {
			workflows = dao.searchByName(name);
			Collections.sort(workflows, modifiedDateComparator);
			return workflows;
		}

		// todo: implemented created and modified search APIs
		return new ArrayList<>();
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
}

class ModifiedDateComparator implements Comparator<Workflow> {
	@Override
	public int compare(Workflow w1, Workflow w2) {
		return w1.modifiedDate.compareTo(w2.modifiedDate);
	}
}