package marxo.controller;

import marxo.bean.Workflow;
import marxo.dao.WorkflowDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("workflow{:s?}")
public class WorkflowController extends GenericController<Workflow, WorkflowDao> {
	@Autowired
	public WorkflowController(WorkflowDao dao) {
		super(dao);
	}
}
