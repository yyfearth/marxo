package marxo.tool;

import com.mongodb.*;
import marxo.Bean.Workflow;
import marxo.data.MongoDbConnector;

import java.util.ArrayList;

/**
 * This class is used to generator example data for development.
 */
public class DataGenerator {
	public static void main(String[] args) {
		MongoClient mongoClient = MongoDbConnector.getMongoClient();
		DB db = mongoClient.getDB("marxo");
		DBCollection dbCollection = db.getCollection("workflows");

		DBCursor cursor = dbCollection.find(new BasicDBObject("IsMocked", true));
		while (cursor.hasNext()) {
			dbCollection.remove(cursor.next());
		}

		ArrayList<Workflow> workflows = new ArrayList<Workflow>();

		for (int i = 0; i < 10; i++) {
			Workflow w = new Workflow();
			w.setName("Workflow " + i);
			w.setIsMocked(true);
			workflows.add(w);
		}

		WriteResult writeResult = dbCollection.insert(TypeTool.<Workflow, DBObject>convert(workflows));
		mongoClient.close();

		System.out.println("WriteResult: " + writeResult);
		System.out.println("Created " + writeResult.getN() + " documents to workflows.");
	}
}
