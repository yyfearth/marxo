package marxo.restlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import marxo.bean.Link;
import marxo.dao.WorkflowDao;
import marxo.restlet.exception.EntityNotFoundException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("workflows/{wfId}/links")
public class NestedLinkRestlet implements Restlet {

	LinkRestlet linkRestlet;
	WorkflowDao workflowDao;

	@GET
	@Path(BasicRestlet.ID_PATH)
	@Produces(MediaType.APPLICATION_JSON)
	public Link get(@PathParam("wfId") String workflowId, @PathParam("id") String id) throws JsonProcessingException {
		// checkWorkflow(workflowId);

		Link link = linkRestlet.get(id);
		if (link.getJsonWorkflowId().equals(workflowId)) {
			return link;
		} else {
			throw new EntityNotFoundException();
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Link> findAll(@PathParam("wfId") String workflowId) throws JsonProcessingException {
		checkWorkflow(workflowId);
		return linkRestlet.dao.findBy("workflowId", workflowId);
	}

	protected void checkWorkflow(String workflowId) throws EntityNotFoundException {
		if (!workflowDao.exists(workflowId)) {
			throw new EntityNotFoundException();
		}
	}

	public WorkflowDao getWorkflowDao() {
		return workflowDao;
	}

	public void setWorkflowDao(WorkflowDao workflowDao) {
		this.workflowDao = workflowDao;
	}

	public LinkRestlet getLinkRestlet() {
		return linkRestlet;
	}

	public void setLinkRestlet(LinkRestlet linkRestlet) {
		this.linkRestlet = linkRestlet;
	}
}
