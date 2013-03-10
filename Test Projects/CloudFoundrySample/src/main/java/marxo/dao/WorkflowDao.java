package marxo.dao;

import com.github.jmkgreen.morphia.Datastore;
import com.github.jmkgreen.morphia.Key;
import com.mongodb.*;
import marxo.Bean.Workflow;
import marxo.data.MongoDbConnector;

import java.util.UUID;

public class WorkflowDao implements IDao<Workflow> {
	Datastore datastore;

	public WorkflowDao() {
		datastore = MongoDbConnector.getDatastore();
	}

	@Override
	public boolean create(Workflow workflow) {
//		MongoClient mongoClient = MongoDbConnector.getMongoClient();
//		DB db = mongoClient.getDB("marxo");
//		DBCollection collection = db.getCollection("workflows");
//
//		WriteResult writeResult = collection.insert(workflow);
//
//		mongoClient.close();
//
//		return writeResult.getError() == null;
		Key<Workflow> key = datastore.save(workflow);

		if (key == null) {
			System.out.println("key == null");
			return false;
		}

		return true;
	}

	@Override
	public Workflow read(UUID id) {
//		MongoClient mongoClient = MongoDbConnector.getMongoClient();
//		DB db = mongoClient.getDB("marxo");
//		DBCollection collection = db.getCollection("workflows");
//
//		try {
//			Workflow workflow = (Workflow) collection.findOne(new BasicDBObject("id", id));
//
//			return workflow;
//		} catch (MongoException e) {
//			e.printStackTrace();
//			return null;
//		} finally {
//			mongoClient.close();
//		}

//		datastore.
		return null;
	}

	@Override
	public boolean update(Workflow obj) {
		return false;
	}

	@Override
	public boolean createOrUpdate(Workflow obj) {
		return false;
	}

	@Override
	public boolean delete(UUID id) {
		return false;
	}

	@Override
	public Workflow findOne(UUID id) {
		return null;
	}

	@Override
	public Workflow[] find(BasicDBObject query) {
		return new Workflow[0];
	}

	@Override
	public boolean removeAll() {
		MongoClient mongoClient = MongoDbConnector.getMongoClient();
		DB db = mongoClient.getDB("marxo");
		DBCollection collection = db.getCollection("workflows");

		try {
			collection.drop();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mongoClient.close();
		}

		return false;
	}
}
