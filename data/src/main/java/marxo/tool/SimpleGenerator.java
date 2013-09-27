package marxo.tool;

import marxo.bean.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.ArrayList;

/**
 * It generates entities without the relationship.
 */
public class SimpleGenerator extends BasicGenerator {
	public static void main(String[] args) {
		final Logger logger = LoggerFactory.getLogger(SimpleGenerator.class);

		ApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"mongo-configuration.xml"});
		MongoTemplate mongoTemplate = context.getBean(MongoTemplate.class);

		// Tenant
		{
			mongoTemplate.dropCollection(Tenant.class);

			ArrayList<Tenant> tenants = new ArrayList<Tenant>();

			for (int i = 1; i <= 10; i++) {
				Tenant t = new Tenant();
				t.setName(getRandomProjectName());
				t.fillWithDefaultValues();
				tenants.add(t);
			}

			mongoTemplate.insert(tenants, Tenant.class);

			logger.debug("Created " + tenants.size() + " documents to Tenant collection.");
		}

		// User
		{
			mongoTemplate.dropCollection(User.class);

			ArrayList<User> users = new ArrayList<User>();

			for (int i = 1; i <= 10; i++) {
				User u = new User(new ObjectId(), getRandomHumanName());
				u.fillWithDefaultValues();
				users.add(u);
			}

			mongoTemplate.insert(users, User.class);

			logger.debug("Created " + users.size() + " documents to User collection.");
		}

		// Workflow
		{
			mongoTemplate.dropCollection(Workflow.class);

			ArrayList<Workflow> workflows = new ArrayList<Workflow>();

			for (int i = 1; i <= 10; i++) {
				Workflow w = new Workflow();
				w.setId(new ObjectId());
				w.setTitle("Workflow " + i);
				w.setName(getRandomProjectName());
				w.setDescription(RandomStringUtils.randomAlphanumeric(10));
				w.fillWithDefaultValues();
				workflows.add(w);
			}

			mongoTemplate.insert(workflows, Workflow.class);

			logger.debug("Created " + workflows.size() + " documents to Workflow collection.");
		}

		// Project
		{
			mongoTemplate.dropCollection(Project.class);

			ArrayList<Project> projects = new ArrayList<Project>();

			for (int i = 1; i <= 10; i++) {
				Project p = new Project();
				p.setTitle("Project " + i);
				p.setName(getRandomProjectName());
				p.setDescription(RandomStringUtils.randomAlphanumeric(10));
				p.fillWithDefaultValues();
				projects.add(p);
			}

			mongoTemplate.insert(projects, Project.class);

			logger.debug("Created " + projects.size() + " documents to Project collection.");
		}

		// Node
		{
			mongoTemplate.dropCollection(Node.class);

			ArrayList<Node> nodes = new ArrayList<Node>();

			for (int i = 1; i <= 10; i++) {
				Node p = new Node(new ObjectId());
				p.setTitle("Node " + i);
				p.setName(getRandomProjectName());
				p.setDescription(RandomStringUtils.randomAlphanumeric(10));
				p.fillWithDefaultValues();
				nodes.add(p);
			}

			mongoTemplate.insert(nodes, Node.class);

			logger.debug("Created " + nodes.size() + " documents to Project collection.");
		}
	}

//	static <T extends BasicEntity> ArrayList<T> getList(Class<T> tClass, int number) {
//		ArrayList<T> list = new ArrayList<>(number);
//		return list;
//	}
}
