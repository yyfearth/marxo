package marxo.dao;

import com.mongodb.*;
import marxo.Bean.Workflow;
import marxo.data.MongoDbConnector;

import java.util.UUID;

public class WorkflowDao implements IDao<Workflow> {

	public WorkflowDao() {
	}

	@Override
	public boolean create(Workflow obj) {
		MongoClient mongoClient = MongoDbConnector.getMongoClient();
		DB db = mongoClient.getDB("marxo");
		DBCollection collection = db.getCollection("workflows");

		WriteResult writeResult = collection.insert(obj);

		mongoClient.close();

		return writeResult.getError() == null;
	}

	@Override
	public Workflow read(UUID id) {
		MongoClient mongoClient = MongoDbConnector.getMongoClient();
		DB db = mongoClient.getDB("marxo");
		DBCollection collection = db.getCollection("workflows");

		try {
			Workflow workflow = (Workflow) collection.findOne(new BasicDBObject("id", id));

			return workflow;
		} catch (MongoException e) {
			e.printStackTrace();
			return null;
		} finally {
			mongoClient.close();
		}
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
