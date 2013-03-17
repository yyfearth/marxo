package marxo.data;

import com.github.jmkgreen.morphia.Datastore;
import com.github.jmkgreen.morphia.Morphia;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import java.net.UnknownHostException;
import java.util.List;

/**
 * According to <a href="http://docs.mongodb.org/ecosystem/tutorial/getting-started-with-java-driver/">official document</a>, MongoClient class is new since 2.10.0.
 */
public class MongoDbConnector {
	static MongoClient mongoClient = null;

	/**
	 * Get a connected mongo client as singleton.
	 *
	 * @return the connected client
	 */
	public static MongoClient getMongoClient() {
		if (mongoClient != null) {
			return mongoClient;
		}

		/**
		 * In order to read the connection credential from a virtualized environment such as Cloud Foundry or AppFog, the program needs to read specific parameters from the environment.
		 * @see http://docs.cloudfoundry.com/services/mysql/mysql.html#the-vcapservices-environment-variable
		 */
		String serviceJson = System.getenv("VCAP_SERVICES");
		MongoClientURI uri = null;

		try {
			if (serviceJson == null) {
				mongoClient = new MongoClient("localhost", 27017);

				List<String> nameList = mongoClient.getDatabaseNames();
				for (String name : nameList) {
					System.out.println("Name: " + name);
				}
			} else {
				System.out.println("System.getenv(\"VCAP_SERVICES\"):\n" + serviceJson);

//				JSONObject services = (JSONObject) JSONValue.parse(serviceJson);
//				JSONArray mongoServices = (JSONArray) services.get("data-2.0");
//				JSONObject mongoService = (JSONObject) mongoServices.get(0);
//				JSONObject credential = (JSONObject) mongoService.get("credentials");
//				String url = (String) credential.get("url");
//				String dbName = (String) credential.get("db");

//				mongoClient = new MongoClient(new MongoClientURI(url));
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return null;
		}

		return mongoClient;
	}

	public static void destoryMongoClient() {
		if (mongoClient == null) {
			return;
		}

		mongoClient.close();
		mongoClient = null;

		if (datastore != null) {
			datastore = null;
		}
	}

	static Datastore datastore = null;

	public static Datastore getDatastore() {
		return getDatastore("marxo");
	}

	public static Datastore getDatastore(String databaseName) {
		if (datastore != null) {
			return datastore;
		}

		return datastore = new Morphia().createDatastore(getMongoClient(), databaseName);
	}
}
