//package marxo.data;
//
//import com.mongodb.DB;
//import com.mongodb.DBCollection;
//import com.mongodb.MongoClient;
//import org.testng.annotations.AfterMethod;
//import org.testng.annotations.BeforeMethod;
//import org.testng.annotations.Test;
//
//import java.util.Set;
//
//public class MongoDbConnectorTest {
//	@BeforeMethod
//	public void setUp() throws Exception {
//
//	}
//
//	@AfterMethod
//	public void tearDown() throws Exception {
//
//	}
//
//	@Test
//	public void testGetMongoClient() throws Exception {
//		MongoClient mongoClient = MongoDbConnector.getMongoClient();
//		DB db = mongoClient.getDB("marxo");
//		Set<String> collectionNames = db.getCollectionNames();
//		DBCollection dbCollection = db.getCollection("things");
//	}
//
//	@Test
//	public void testGetDb() throws Exception {
//
//	}
//
//	@Test
//	public void testIsConnected() throws Exception {
//
//	}
//
//	@Test
//	public void testGetConnectedConnector() throws Exception {
//
//	}
//
//	@Test
//	public void testConnect() throws Exception {
//
//	}
//}
