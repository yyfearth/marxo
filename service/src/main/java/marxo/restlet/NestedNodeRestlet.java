//package marxo.restlet;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import marxo.bean.Node;
//import marxo.dao.WorkflowDao;
//import marxo.restlet.exception.EntityNotFoundException;
//
//import javax.ws.rs.GET;
//import javax.ws.rs.Path;
//import javax.ws.rs.PathParam;
//import javax.ws.rs.Produces;
//import javax.ws.rs.core.MediaType;
//import java.util.List;
//
//@Path("workflows/{wfId}/nodes")
//public class NestedNodeRestlet implements Restlet {
//
//	NodeRestlet nodeRestlet;
//	WorkflowDao workflowDao;
//
//	@GET
//	@Path(BasicRestlet.ID_PATH)
//	@Produces(MediaType.APPLICATION_JSON)
//	public Node get(@PathParam("wfId") String workflowId, @PathParam("id") String id) throws JsonProcessingException {
//		// checkWorkflow(workflowId);
//		Node node = nodeRestlet.get(id);
//		if (node.getJsonWorkflowId().equals(id)) {
//			return node;
//		} else {
//			throw new EntityNotFoundException();
//		}
//	}
//
//	@GET
//	@Produces(MediaType.APPLICATION_JSON)
//	public List<Node> findAll(@PathParam("wfId") String workflowId) throws JsonProcessingException {
//		checkWorkflow(workflowId);
//		return nodeRestlet.dao.findBy("workflowId", workflowId);
//	}
//
//	protected void checkWorkflow(String workflowId) throws EntityNotFoundException {
//		if (!workflowDao.exists(workflowId)) {
//			throw new EntityNotFoundException();
//		}
//	}
//
//	public WorkflowDao getWorkflowDao() {
//		return workflowDao;
//	}
//
//	public void setWorkflowDao(WorkflowDao workflowDao) {
//		this.workflowDao = workflowDao;
//	}
//
//	public NodeRestlet getNodeRestlet() {
//		return nodeRestlet;
//	}
//
//	public void setNodeRestlet(NodeRestlet nodeRestlet) {
//		this.nodeRestlet = nodeRestlet;
//	}
//}
