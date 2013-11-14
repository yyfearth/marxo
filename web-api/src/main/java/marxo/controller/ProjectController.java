package marxo.controller;

import marxo.dao.WorkflowDao;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("project{:s?}")
public class ProjectController extends WorkflowController {
	@Override
	public void preHandle() {
		super.preHandle();
		dao = new WorkflowDao(user.tenantId, true);
	}
}
