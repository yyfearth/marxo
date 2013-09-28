package marxo.controller;

import marxo.bean.Workflow;
import marxo.dao.WorkflowDao;
import marxo.exception.*;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("workflow{:s?}")
public class WorkflowController extends BasicController<Workflow, WorkflowDao> {
	@Autowired
	WorkflowDao workflowDao;

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public List<Workflow> getAll() {
		return workflowDao.findAll();
	}

	@RequestMapping(value = "/", method = RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(HttpStatus.CREATED)
	public Workflow create(@Valid @RequestBody Workflow entity) throws Exception {
		if (workflowDao.exists(entity.getId())) {
			throw new EntityExistsException(entity.getId());
		}

		try {
			workflowDao.save(entity);
		} catch (ValidationException ex) {
			// todo: add error message
			throw new EntityInvalidException(entity.getId(), "not implemented");
		}

		return entity;
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public Workflow read(@PathVariable String id) {
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
	public Workflow update(@Valid @PathVariable String id, @Valid @RequestBody Workflow workflow) {
		if (!ObjectId.isValid(id)) {
			throw new InvalidObjectIdException(id);
		}

		ObjectId objectId = new ObjectId(id);

		// todo: check consistency of given id and entity id.
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
	public Workflow delete(@PathVariable String id) {
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
