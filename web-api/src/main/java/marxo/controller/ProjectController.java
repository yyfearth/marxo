package marxo.controller;

import marxo.entity.workflow.Workflow;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
@RequestMapping("project{:s?}")
public class ProjectController extends WorkflowController {
	@Override
	protected boolean isProject() {
		return true;
	}

	@Override
	public Workflow create(@Valid @RequestBody Workflow entity, HttpServletResponse response) throws Exception {
		entity.isProject = true;
		return super.create(entity, response);
	}
}
