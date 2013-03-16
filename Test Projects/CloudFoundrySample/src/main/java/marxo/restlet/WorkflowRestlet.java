package marxo.restlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.WriteResult;
import marxo.bean.Workflow;
import marxo.dao.WorkflowDao;
import org.bson.types.ObjectId;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

@Path("workflows")
public class WorkflowRestlet {
	WorkflowDao workflowDao = new WorkflowDao();

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response postWorkflow(Workflow workflow) {
		workflow.setId(new ObjectId());
		Date now = new Date();
		workflow.setCreatedDate(now);
		workflow.setModifiedDate(now);

		try {
			workflowDao.save(workflow);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ErrorWebApplicationException(ErrorType.UNKNOWN, "Unable to save the workflow");
		}

		String path = "/" + workflow.getId();

		try {
			return Response.created(new URI(path)).entity(workflow).build();
		} catch (URISyntaxException e) {
			e.printStackTrace();
			throw new ErrorWebApplicationException(ErrorType.UNKNOWN, "Unable to construct the URI: " + path);
		}
	}

	@GET
	@Path("{workflowId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getWorkflow(@PathParam("workflowId") String workflowId) throws JsonProcessingException {
		if (ObjectId.isValid(workflowId) == false) {
			throw new ErrorWebApplicationException(ErrorType.ID_NOT_PROPERLY_FORMATTED);
		}

		Workflow workflow = workflowDao.get(new ObjectId(workflowId));

		if (workflow == null) {
			throw new ErrorWebApplicationException(ErrorType.ENTITY_NOT_FOUND);
		}

		return Response.ok(workflow).build();
	}

	@PUT
	@Path("{workflowId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response putWorkflow(@PathParam("workflowId") String workflowId, Workflow newWorkflow) {
		if (ObjectId.isValid(workflowId) == false) {
			throw new ErrorWebApplicationException(ErrorType.ID_NOT_PROPERLY_FORMATTED);
		}

		System.out.println("Does exist? " + workflowDao.exists("id", workflowId));

		WriteResult writeResult = workflowDao.deleteById(new ObjectId(workflowId));

		System.out.println("getN: " + writeResult.getN());

		if (writeResult.getError() != null) {
			System.out.println(writeResult.getError());
			throw new ErrorWebApplicationException(ErrorType.UNKNOWN);
		}

		try {
			if (newWorkflow.getId() == null) {
				newWorkflow.setJsonId(workflowId);
			}

			workflowDao.save(newWorkflow);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ErrorWebApplicationException(ErrorType.UNKNOWN);
		}

		return Response.ok().entity(newWorkflow).build();
	}

	@DELETE
	@Path("{workflowId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteWorkflow(@PathParam("workflowId") String workflowId) {
		if (ObjectId.isValid(workflowId) == false) {
			throw new ErrorWebApplicationException(ErrorType.ID_NOT_PROPERLY_FORMATTED);
		}

		String errorMessage = workflowDao.deleteById(new ObjectId(workflowId)).getError();

		if (errorMessage != null) {
			throw new ErrorWebApplicationException(ErrorType.UNKNOWN);
		}

		return Response.ok().build();
	}
}
