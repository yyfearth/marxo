package marxo.restlet;

import com.google.gson.Gson;
import marxo.Bean.Project;
import org.apache.commons.lang.StringUtils;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.UUID;

@Path("/projects/{projectId}")
public class ProjectRestlet {
	@GET
	public String getProject(@PathParam("projectId") String projectId) {
		Gson gson = new Gson();

		if (StringUtils.isEmpty(projectId)) {
			return gson.toJson(new Object());
		}

		Project project = new Project();
		project.id = UUID.fromString(projectId);

		return gson.toJson(project);
	}
}
