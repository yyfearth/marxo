package test;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.net.UnknownHostException;

public class MongoConnector {
	public MongoClient getMongoClient() {
		return mongoClient;
	}

	private MongoClient mongoClient;

	public DB getDb() {
		return db;
	}

	private DB db;

	public boolean isConnected() {
		return isConnected;
	}

	private boolean isConnected = false;

	public static MongoConnector getConnectedConnector() {
		MongoConnector mongoConnector = new MongoConnector();
		mongoConnector.connect();
		return mongoConnector;
	}

	public boolean connect() {
		String serviceJson = System.getenv("VCAP_SERVICES");

		try {
			if (serviceJson == null) {
				mongoClient = new MongoClient("localhost", 27017);
				db = mongoClient.getDB("db");
			} else {
				System.out.println("System.getenv(\"VCAP_SERVICES\"):\n" + serviceJson);

				JSONObject services = (JSONObject) JSONValue.parse(serviceJson);
				JSONArray mongoServices = (JSONArray) services.get("mongodb-2.0");
				JSONObject mongoService = (JSONObject) mongoServices.get(0);
				JSONObject credential = (JSONObject) mongoService.get("credentials");
				String url = (String) credential.get("url");
				String dbName = (String) credential.get("db");

				mongoClient = new MongoClient(new MongoClientURI(url));
				db = mongoClient.getDB(dbName);
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return isConnected = false;
		}

		return isConnected = true;
	}
}
