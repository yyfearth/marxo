package marxo.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jmkgreen.morphia.Datastore;
import com.github.jmkgreen.morphia.Morphia;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import java.io.IOException;
import java.net.UnknownHostException;

/**
 * According to <a href="http://docs.mongodb.org/ecosystem/tutorial/getting-started-with-java-driver/">official document</a>, MongoClient class is new since 2.10.0.
 */
public class MongoDbConnector {
	static MongoClientURI uri;

	/**
	 * Get a connected mongo client as singleton.
	 *
	 * @return the connected client
	 */
	public static MongoClientURI getMongoUri() {
		if (uri != null) {
			return uri;
		}

		/**
		 * In order to read the connection credential from a virtualized environment such as Cloud Foundry or AppFog, the program needs to read specific parameters from the environment.
		 * @see http://docs.cloudfoundry.com/services/mysql/mysql.html#the-vcapservices-environment-variable
		 */
		String serviceJson = System.getenv("VCAP_SERVICES");

		try {
			if (serviceJson == null) {
				uri = new MongoClientURI("mongodb://localhost:27017/marxo");
			} else {
				System.out.println("System.getenv(\"VCAP_SERVICES\"):\n" + serviceJson);
				// mongodb://08b9893e-6ba2-43f1-8832-00089d89853d:24278a9c-92aa-4947-b7c1-ef9c6db73d4d@172.30.48.67:25273/db

				ObjectMapper m = new ObjectMapper();
				JsonNode rootNode = m.readTree(serviceJson);
				JsonNode credential = rootNode.get("mongodb-2.0").get(0).get("credentials");
				String url = credential.get("url").asText();
				System.out.println("Got DB URL: " + url);

				uri = new MongoClientURI(url);
			}
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			uri = null;
		} catch (IOException e) {
			e.printStackTrace();
			uri = null;
		}

		return uri;
	}

	static MongoClient mongoClient = null;

	public static MongoClient getMongoClient() {
		if (mongoClient != null) {
			return mongoClient;
		}
		try {
			mongoClient = new MongoClient(getMongoUri());
		} catch (UnknownHostException e) {
			e.printStackTrace();
			mongoClient = null;
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
		if (datastore != null) {
			return datastore;
		}

		return datastore = new Morphia().createDatastore(getMongoClient(), uri.getDatabase());
	}
}
