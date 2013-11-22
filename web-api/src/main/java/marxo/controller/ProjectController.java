package marxo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("project{:s?}")
public class ProjectController extends WorkflowController {
	@Override
	protected boolean isProject() {
		return true;
	}
}
