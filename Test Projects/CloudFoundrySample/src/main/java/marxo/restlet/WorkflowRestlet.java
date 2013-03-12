package marxo.restlet;

import com.google.gson.Gson;
import marxo.Bean.Workflow;
import marxo.dao.WorkflowDao;
import org.apache.commons.lang.StringUtils;
import org.bson.types.ObjectId;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;

@Path("/workflows/{workflowId}")
public class WorkflowRestlet {

	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public Workflow getWorkflow(@PathParam("workflowId") String workflowId) {
		if (ObjectId.isValid(workflowId) == false) {

		}

		WorkflowDao workflowDao = new WorkflowDao();
		workflowDao.get()
	}

	@POST
	public String getWorkflow(@PathParam("workflowId") String workflowId) {
		Gson gson = new Gson();

		if (StringUtils.isEmpty(workflowId)) {
			return gson.toJson(new Object());
		}

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("workflowID", workflowId);
		map.put("name", "mocked");

		return gson.toJson(map);
	}

	@PUT
	public String getWorkflow(@PathParam("workflowId") String workflowId) {
		Gson gson = new Gson();

		if (StringUtils.isEmpty(workflowId)) {
			return gson.toJson(new Object());
		}

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("workflowID", workflowId);
		map.put("name", "mocked");

		return gson.toJson(map);
	}

	@DELETE
	public String getWorkflow(@PathParam("workflowId") String workflowId) {
		Gson gson = new Gson();

		if (StringUtils.isEmpty(workflowId)) {
			return gson.toJson(new Object());
		}

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("workflowID", workflowId);
		map.put("name", "mocked");

		return gson.toJson(map);
	}
}
