package marxo.controller;

import marxo.bean.Workflow;
import marxo.dao.WorkflowDao;
import marxo.exception.*;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("workflow{:s?}")
public class WorkflowController extends BasicController<Workflow, WorkflowDao> {
	@Autowired
	ApplicationContext applicationContext;
	@Autowired
	WorkflowDao workflowDao;

	@PostConstruct
	void report() {
		logger.info(this.getClass().getSimpleName() + " started");
	}

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public List<Workflow> getWorkflows() {
		return workflowDao.findAll();
	}

	@RequestMapping(value = "/", method = RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(HttpStatus.CREATED)
	public Workflow createWorkflow(@Valid @RequestBody Workflow workflow) throws Exception {
		if (workflowDao.exists(workflow.getId())) {
			throw new EntityExistsException(workflow.getId());
		}

		try {
			workflowDao.save(workflow);
		} catch (ValidationException ex) {
			// todo: add error message
			throw new EntityInvalidException(workflow.getId(), "not implemented");
		}

		return workflow;
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public Workflow readWorkflow(@PathVariable String id) {
		if (!ObjectId.isValid(id)) {
			throw new InvalidObjectIdException(id);
		}

		ObjectId objectId = new ObjectId(id);
		Workflow workflow = workflowDao.get(objectId);

		if (workflow == null) {
			throw new EntityNotFoundException(objectId);
		}

		return workflow;
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	// fixme: get ObjectId from Spring MVC, and let the global validator do the validation.
	// review: is the parameter 'id' necessary?
	public Workflow updateWorkflow(@Valid @PathVariable String id, @Valid @RequestBody Workflow workflow) {
		if (!ObjectId.isValid(id)) {
			throw new InvalidObjectIdException(id);
		}

		ObjectId objectId = new ObjectId(id);

		// todo: check consistency of given id and workflow id.
		Workflow oldWorkflow = workflowDao.get(objectId);

		if (oldWorkflow == null) {
			throw new EntityNotFoundException(objectId);
		}

		if (workflow.getName() != null) {
			oldWorkflow.setName(workflow.getName());
		}

		if (workflow.getTitle() != null) {
			oldWorkflow.setTitle(workflow.getTitle());
		}

		if (workflow.getCreatedByUserId() != null) {
			oldWorkflow.setCreatedByUserId(workflow.getCreatedByUserId());
		}

		if (workflow.getModifiedByUserId() != null) {
			oldWorkflow.setModifiedByUserId(workflow.getModifiedByUserId());
		}

		try {
			workflowDao.save(oldWorkflow);
		} catch (ValidationException e) {
//			e.reasons.toString()
//			throw new EntityInvalidException(objectId, );
		}

		return oldWorkflow;
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	// fixme: get ObjectId from Spring MVC, and let the global validator do the validation.
	public Workflow deleteWorkflow(@PathVariable String id) {
		if (!ObjectId.isValid(id)) {
			throw new InvalidObjectIdException(id);
		}

		ObjectId objectId = new ObjectId(id);
		Workflow workflow = workflowDao.deleteById(objectId);

		if (workflow == null) {
			throw new EntityNotFoundException(objectId);
		}

		return workflow;
	}
}
