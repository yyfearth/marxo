package marxo.controller;

import marxo.bean.Workflow;
import marxo.dao.WorkflowDao;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/workflow{:s?}")
public class WorkflowController extends BasicController<Workflow, WorkflowDao> {
	@Autowired
	private WorkflowDao workflowDao;

	public void setWorkflowDao(WorkflowDao workflowDao) {
		this.workflowDao = workflowDao;
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public Workflow getWorkflow(@PathVariable String id) throws Exception {
		ObjectId objectId = new ObjectId(id);
		Workflow workflow = workflowDao.get(objectId);

		if (workflow == null) {
			throw new Exception("Oops");
		}

		return workflow;
	}

	@RequestMapping
	@ResponseBody
	public List<Integer> getWorkflow() {
		List<Integer> list = new ArrayList<>(10);

		for (int i = 0; i < 10; i++) {
			list.add(i);
		}

		return list;
	}
}
