package marxo.restlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import marxo.bean.Workflow;
import marxo.dao.WorkflowDao;
import marxo.tool.DataGenerator;
import org.bson.types.ObjectId;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;

// Todo: refactoring error response.
@Path("/workflows")
public class WorkflowRestlet {

	@POST
	@Produces({MediaType.APPLICATION_JSON})
	public Response postWorkflow() throws URISyntaxException {
		Workflow workflow = new Workflow();
		workflow.setName(DataGenerator.getRandomProjectName());

		try {
			WorkflowDao workflowDao = new WorkflowDao();
			workflowDao.save(workflow);
		} catch (Exception e) {
			e.printStackTrace();
			return Response.serverError().entity(new ErrorJson(ErrorType.Unknown)).build();
		}

		return Response.created(new URI("/" + workflow.getId())).build();
	}

	@GET
	@Path("/{workflowId}")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getWorkflow(@PathParam("workflowId") String workflowId) throws JsonProcessingException {
		if (ObjectId.isValid(workflowId) == false) {
			return Response.status(Response.Status.NOT_FOUND).entity(new ErrorJson(ErrorType.InvalidRequest)).build();
		}

		WorkflowDao workflowDao = new WorkflowDao();
		Workflow workflow = workflowDao.get(new ObjectId(workflowId));

		if (workflow == null) {
			return Response.status(Response.Status.NOT_FOUND).entity(new ErrorJson(ErrorType.EntityNotFound)).build();
		}

		return Response.ok(workflow).build();
	}

	@PUT
	@Path("/{workflowId}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public Response puttWorkflow(@PathParam("workflowId") String workflowId, Workflow newWorkflow) {
		if (ObjectId.isValid(workflowId) == false) {
			return Response.status(Response.Status.NOT_FOUND).entity(new ErrorJson(ErrorType.InvalidRequest)).build();
		}

		WorkflowDao workflowDao = new WorkflowDao();
		String errorMessage = workflowDao.deleteById(new ObjectId(workflowId)).getError();

		if (errorMessage != null) {
			System.out.println(errorMessage);
			return Response.serverError().entity(new ErrorJson(ErrorType.Unknown)).build();
		}

		try {
			workflowDao.save(newWorkflow);
		} catch (Exception e) {
			e.printStackTrace();
			return Response.serverError().entity(new ErrorJson(ErrorType.Unknown)).build();
		}

		return Response.ok().build();
	}

	@DELETE
	@Path("/{workflowId}")
	@Produces({MediaType.APPLICATION_JSON})
	public Response deleteWorkflow(@PathParam("workflowId") String workflowId) {
		if (ObjectId.isValid(workflowId) == false) {
			return Response.status(Response.Status.NOT_FOUND).entity(new ErrorJson(ErrorType.InvalidRequest)).build();
		}

		WorkflowDao workflowDao = new WorkflowDao();
		String errorMessage = workflowDao.deleteById(new ObjectId(workflowId)).getError();

		if (errorMessage != null) {
			System.out.println(errorMessage);
			return Response.serverError().entity(new ErrorJson(ErrorType.Unknown)).build();
		}

		return Response.ok().build();
	}
}
