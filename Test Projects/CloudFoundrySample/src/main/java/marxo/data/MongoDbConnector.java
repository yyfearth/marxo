package marxo.data;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.net.UnknownHostException;

public class MongoDbConnector {
	/**
	 * Get a connected mongo client.
	 *
	 * @return the connected client
	 */
	public static MongoClient getMongoClient() {
		/**
		 * In order to read the connection credential from a virtualized environment such as Cloud Foundry or AppFog, the program needs to read specific parameters from the environment.
		 * @see http://docs.cloudfoundry.com/services/mysql/mysql.html#the-vcapservices-environment-variable
		 */
		String serviceJson = System.getenv("VCAP_SERVICES");
		MongoClient mongoClient = null;

		try {
			if (serviceJson == null) {
				mongoClient = new MongoClient("localhost", 27017);
			} else {
				System.out.println("System.getenv(\"VCAP_SERVICES\"):\n" + serviceJson);

				JSONObject services = (JSONObject) JSONValue.parse(serviceJson);
				JSONArray mongoServices = (JSONArray) services.get("data-2.0");
				JSONObject mongoService = (JSONObject) mongoServices.get(0);
				JSONObject credential = (JSONObject) mongoService.get("credentials");
				String url = (String) credential.get("url");
				String dbName = (String) credential.get("db");

				mongoClient = new MongoClient(new MongoClientURI(url));
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return null;
		}

		return mongoClient;
	}
}
