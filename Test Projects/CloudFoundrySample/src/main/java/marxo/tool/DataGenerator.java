package marxo.tool;

import java.util.concurrent.ThreadLocalRandom;

/**
 * This class is used to generator example data for development.
 */
public class DataGenerator {
	public static void main(String[] args) {
//		MongoClient mongoClient = MongoDbConnector.getMongoClient();
//		DB db = mongoClient.getDB("marxo");
//		DBCollection dbCollection = db.getCollection("workflows");
//
//		DBCursor cursor = dbCollection.find(new BasicDBObject("IsMocked", true));
//		while (cursor.hasNext()) {
//			dbCollection.remove(cursor.next());
//		}
//
//		ArrayList<Workflow> workflows = new ArrayList<Workflow>();
//
//		for (int i = 0; i < 10; i++) {
//			Workflow w = new Workflow();
//			w.setName("Workflow " + i);
//			workflows.add(w);
//		}
//
//		WriteResult writeResult = dbCollection.insert(TypeTool.<Workflow, DBObject>convert(workflows));
//		mongoClient.close();
//
//		System.out.println("WriteResult: " + writeResult);
//		System.out.println("Created " + writeResult.getN() + " documents to workflows.");
	}

	static String[] humanNames = {"Jacob", "Mason", "William", "Jayden", "Noah", "Michael", "Ethan", "Alexander", "Aiden", "Daniel", "Anthony", "Matthew", "Elijah", "Joshua", "Liam", "Andrew", "James", "David", "Benjamin", "Logan", "Christopher", "Joseph", "Jackson", "Gabriel", "Ryan", "Samuel", "John", "Nathan", "Lucas", "Christian", "Jonathan", "Caleb", "Dylan", "Landon", "Isaac", "Gavin", "Brayden", "Tyler", "Luke", "Evan", "Carter", "Nicholas", "Isaiah", "Owen", "Jack", "Jordan", "Brandon", "Wyatt", "Julian", "Aaron", "Jeremiah", "Angel", "Cameron", "Connor", "Hunter", "Adrian", "Henry", "Eli", "Justin", "Austin", "Robert", "Charles", "Thomas", "Zachary", "Jose", "Levi", "Kevin", "Sebastian", "Chase", "Ayden", "Jason", "Ian", "Blake", "Colton", "Bentley", "Dominic", "Xavier", "Oliver", "Parker", "Josiah", "Adam", "Cooper", "Brody", "Nathaniel", "Carson", "Jaxon", "Tristan", "Luis", "Juan", "Hayden", "Carlos", "Jesus", "Nolan", "Cole", "Alex", "Max", "Grayson", "Bryson", "Diego", "Jaden",};

	public static String getRandomHumanName() {
		int index = ThreadLocalRandom.current().nextInt(humanNames.length);
		return humanNames[index];
	}

	static String[] projectNames = {"Old Sound", "Gruesome Global Compass", "Lucky Viper", "Full Crystal", "Temporary Digital Vegetable", "Dagger Discarded", "Gutsy Tuna", "Golden Cobra", "Heavy Oyster", "Essential Venom", "Solid Serpent", "Pure Waffle", "Subtle Steel", "Alien Torpedo", "Aggressive Scissors", "Sticky Warehouse", "Maroon Warehouse", "Trombone Shiny", "Timely Scorpion", "Cheerful Backpack", "Rough Scissors", "Black Space", "Maroon Pure Antique", "Helpless Donut", "Grim Moose", "Rainbow Test", "Harsh Bulldozer", "Poseidon Dirty", "Scattered Frozen Hammer", "Screaming Puppet", "Third Hammer", "Unique Poseidon", "Third Helpless Electron", "Stormy Hammer", "Sun Vital", "Surreal Gamma", "Silly Butter", "Helpless Parachute", "Rebel Homeless Alarm", "Golden Puppet", "Ghastly Knife", "Boiling Morning", "Bird Boiling", "Heavy Rough Serpent", "Disappointed Obscure", "Sleepy Electron", "Official Waffle", "Indigo Steel", "Streaming Haystack", "Abandoned Lantern", "Skunk Flying", "Rubber Skilled", "Long Tire", "Vegetable Aberrant", "Small Lion", "Confidential Beam", "Everyday Global Proton", "Homeless Ivory Galaxy", "Deserted Trendy", "Orange Crossbow", "Minimum Crossbow", "Smoke Forgotten", "Helpless Moose", "Digital Space", "Ghastly Running Alpha",};

	public static String getRandomProjectName() {
		int index = ThreadLocalRandom.current().nextInt(projectNames.length);
		return projectNames[index];
	}
}
