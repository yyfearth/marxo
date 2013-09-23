package marxo.controller;

import marxo.bean.Workflow;
import marxo.dao.WorkflowDao;
import marxo.exception.EntityExistsException;
import marxo.exception.EntityNotFoundException;
import marxo.exception.InvalidObjectIdException;
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

	@RequestMapping(value = "/{id}", method = RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(HttpStatus.CREATED)
	// TODO: Validate the given id
	public Workflow createWorkflow(@Valid @PathVariable String id, @Valid @RequestBody Workflow workflow) throws Exception {
		if (!ObjectId.isValid(id)) {
			throw new InvalidObjectIdException(id);
		}

		ObjectId objectId = new ObjectId(id);

		if (workflowDao.exists(objectId)) {
			throw new EntityExistsException(objectId);
		}

		workflow.setId(objectId);
		workflow.reset();

		// TODO: validate the workflow (or do it in the dao?)
		workflowDao.save(workflow);

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
	public Workflow updateWorkflow(@Valid @PathVariable String id, @Valid @RequestBody Workflow workflow) {
		if (!ObjectId.isValid(id)) {
			throw new InvalidObjectIdException(id);
		}

		ObjectId objectId = new ObjectId(id);
		Workflow oldWorkflow = workflowDao.get(objectId);

		if (oldWorkflow == null) {
			throw new EntityNotFoundException(objectId);
		}

		oldWorkflow.setName(workflow.getName());
		oldWorkflow.setTitle(workflow.getTitle());
		oldWorkflow.setCreatedByUserId(workflow.getCreatedByUserId());
		oldWorkflow.setModifiedByUserId(workflow.getModifiedByUserId());

		// TODO: validate the workflow, to maintain the consistency of the data.
		workflowDao.save(oldWorkflow);

		return workflow;
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
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
