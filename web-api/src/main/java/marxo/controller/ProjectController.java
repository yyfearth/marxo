package marxo.controller;

import marxo.dao.WorkflowDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("project{:s?}")
public class ProjectController extends WorkflowController {
	@Autowired
	protected ProjectController(WorkflowDao dao) {
		super(dao);
	}

	@Override
	public void preHandle() {
		super.preHandle();
		daoContext = daoContext.addContext("isProject", true);
	}
}
