package test;

import com.github.jmkgreen.morphia.Datastore;
import com.github.jmkgreen.morphia.Key;
import com.github.jmkgreen.morphia.Morphia;
import com.google.gson.Gson;
import com.mongodb.Mongo;
import com.mongodb.ServerAddress;
import marxo.Bean.Project;
import org.testng.annotations.Test;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Date;

/**
 * Temporary playground. Please have fun.
 */
public class AdHocTest {
	@Test
	public void test() {
		try {

			Mongo mongo = new Mongo(Arrays.asList(
					new ServerAddress("localhost", 27017)
			));

			Datastore datastore = new Morphia().createDatastore(mongo, "marxo");

			Project project = new Project();
			project.name = "Hello world";
			project.lastModifiedDateTime = project.createdDateTime = new Date();

//			Gson gson = new Gson();
//			String json = gson.toJson(project);
//			System.out.println(json);

			Key<Project> key = datastore.save(project);

			System.out.println(key);

			Gson gson = new Gson();
			System.out.println("Gson: " + gson.toJson(project));


		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
}
