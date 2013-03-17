package marxo.dao;

import com.mongodb.Mongo;
import com.mongodb.ServerAddress;

import java.net.UnknownHostException;
import java.util.Arrays;

public class MongoFactory {
	static Mongo mongo;

	public static Mongo getMongo() {
		if (mongo == null) {
			try {
				mongo = new Mongo(Arrays.asList(new ServerAddress("localhost")));
			} catch (UnknownHostException e) {
				e.printStackTrace();
				return null;
			}
		}

		return mongo;
	}
}
