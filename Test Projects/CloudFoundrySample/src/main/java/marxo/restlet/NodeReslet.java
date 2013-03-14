//package marxo.restlet;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import marxo.bean.SharedNode;
//import marxo.bean.Workflow;
//import marxo.dao.NodeDao;
//import marxo.tool.DataGenerator;
//import org.bson.types.ObjectId;
//
//import javax.ws.rs.*;
//import javax.ws.rs.core.MediaType;
//import javax.ws.rs.core.Response;
//import java.net.URI;
//import java.net.URISyntaxException;
//
//public class NodeReslet {
//	NodeDao nodeDao = new NodeDao();
//
//	@POST
//	@Consumes({MediaType.APPLICATION_JSON})
//	@Produces({MediaType.APPLICATION_JSON})
//	public Response postWorkflow() throws URISyntaxException {
//		SharedNode node = new SharedNode();
//		node.setName(DataGenerator.getRandomProjectName());
//
//		try {
//			nodeDao.save(node);
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new ErrorWebApplicationException(ErrorType.Unknown);
//		}
//
//		return Response.created(new URI("/" + node.getId())).entity(node).build();
//	}
//
//	@GET
//	@Path("{workflowId}")
//	@Produces({MediaType.APPLICATION_JSON})
//	public Response getWorkflow(@PathParam("workflowId") String workflowId) throws JsonProcessingException {
//		if (ObjectId.isValid(workflowId) == false) {
//			throw new ErrorWebApplicationException(ErrorType.InvalidRequest);
//		}
//
//		Workflow workflow = nodeDao.get(new ObjectId(workflowId));
//
//		if (workflow == null) {
//			throw new ErrorWebApplicationException(ErrorType.EntityNotFound);
//		}
//
//		return Response.ok(workflow).build();
//	}
//
//	@PUT
//	@Path("{workflowId}")
//	@Consumes({MediaType.APPLICATION_JSON})
//	@Produces({MediaType.APPLICATION_JSON})
//	public Response putWorkflow(@PathParam("workflowId") String workflowId, Workflow newWorkflow) {
//		if (ObjectId.isValid(workflowId) == false) {
//			throw new ErrorWebApplicationException(ErrorType.InvalidRequest);
//		}
//
//		String errorMessage = nodeDao.deleteById(new ObjectId(workflowId)).getError();
//
//		if (errorMessage != null) {
//			System.out.println(errorMessage);
//			throw new ErrorWebApplicationException(ErrorType.Unknown);
//		}
//
//		try {
//			nodeDao.save(newWorkflow);
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new ErrorWebApplicationException(ErrorType.Unknown);
//		}
//
//		return Response.ok().build();
//	}
//
//	@DELETE
//	@Path("{workflowId}")
//	@Produces({MediaType.APPLICATION_JSON})
//	public Response deleteWorkflow(@PathParam("workflowId") String workflowId) {
//		if (ObjectId.isValid(workflowId) == false) {
//			return Response.status(Response.Status.NOT_FOUND).entity(new ErrorJson(ErrorType.InvalidRequest)).build();
//		}
//
//		String errorMessage = nodeDao.deleteById(new ObjectId(workflowId)).getError();
//
//		if (errorMessage != null) {
//			System.out.println(errorMessage);
//			return Response.serverError().entity(new ErrorJson(ErrorType.Unknown)).build();
//		}
//
//		return Response.ok().build();
//	}
//}
