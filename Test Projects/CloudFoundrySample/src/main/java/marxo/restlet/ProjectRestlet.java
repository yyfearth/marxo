package marxo.restlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jmkgreen.morphia.Datastore;
import marxo.bean.Project;
import marxo.data.JsonParser;
import marxo.data.MongoDbConnector;
import org.apache.commons.lang.StringUtils;
import org.bson.types.ObjectId;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.io.IOException;

@Path("/projects/{projectId}")
public class ProjectRestlet {
	@GET
	public String getProject(@PathParam("projectId") String projectId) {
		ObjectMapper objectMapper = JsonParser.getMapper();

		if (StringUtils.isEmpty(projectId)) {
			return "{}";
		}

		Datastore datastore = MongoDbConnector.getDatastore();
		Project project = datastore.find(Project.class, "_id", new ObjectId(projectId)).get();

		try {
			return objectMapper.writeValueAsString(project);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

    @POST
    public String setProject(@PathParam("projectId") String projectId, Project project) {
        return null;
    }
}
