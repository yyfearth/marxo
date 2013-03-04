package test;

import com.google.gson.Gson;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;
import marxo.data.MongoDbConnector;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/test")
public class Test {
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getTestJson() {
		MongoClient mongoClient = MongoDbConnector.getMongoClient();
		DB db = mongoClient.getDB("marxo");
		DBCollection dbCollection = db.getCollection("things");

		Gson gson = new Gson();

		return JSON.serialize(dbCollection.find().toArray());
	}
}
