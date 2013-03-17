package marxo.restlet;

import marxo.bean.Workflow;
import marxo.dao.WorkflowDao;

import javax.ws.rs.Path;

@Path("workflows")
public class WorkflowRestlet extends BasicRestlet<Workflow, WorkflowDao> {

	public WorkflowRestlet() throws IllegalAccessException, InstantiationException {
	}
}
