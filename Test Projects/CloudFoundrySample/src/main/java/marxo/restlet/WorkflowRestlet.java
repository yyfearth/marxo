package marxo.restlet;

import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.HashMap;

@Path("/workflows/{workflowId}")
public class WorkflowRestlet {
	@GET
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
