package marxo.tool;

import com.mongodb.*;
import marxo.Bean.Workflow;
import marxo.data.MongoDbConnector;

import java.util.ArrayList;
import java.util.List;

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
//		Workflow[] workflows = new Workflow[]{
//				new Workflow("Jacob"),
//				new Workflow("Mason"),
//				new Workflow("William"),
//				new Workflow("Jayden"),
//				new Workflow("Noah"),
//				new Workflow("Michael"),
//				new Workflow("Ethan"),
//				new Workflow("Alexander"),
//				new Workflow("Aiden"),
//				new Workflow("Daniel"),
//				new Workflow("Sophia"),
//				new Workflow("Isabella"),
//				new Workflow("Emma"),
//				new Workflow("Olivia"),
//				new Workflow("Ava"),
//				new Workflow("Emily"),
//				new Workflow("Abigail"),
//				new Workflow("Madison"),
//				new Workflow("Mia"),
//				new Workflow("Chloe"),
//		};

		for (int i = 0; i < 10; i++) {
			Workflow w = new Workflow();
			w.setName("Workflow " + i);
			w.setIsMocked(true);
			workflows.add(w);
		}

		List<DBObject> list = new ArrayList<DBObject>();

		for (Workflow w : workflows) {
			list.add((DBObject) w);
		}

		WriteResult writeResult = dbCollection.insert(list);
		mongoClient.close();

		System.out.println("WriteResult: " + writeResult);
		System.out.println("Created " + writeResult.getN() + " documents to workflows.");
	}
}
