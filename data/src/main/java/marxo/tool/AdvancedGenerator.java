package marxo.tool;

import marxo.entity.*;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;

import javax.crypto.SecretKeyFactory;
import javax.xml.bind.DatatypeConverter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

public class AdvancedGenerator extends BasicGenerator {
	static final Logger logger = LoggerFactory.getLogger(AdvancedGenerator.class);

	public static void main(String[] args) {
		ApplicationContext dataContext = new ClassPathXmlApplicationContext("mongo-configuration.xml");
		MongoTemplate mongoTemplate = dataContext.getBean(MongoTemplate.class);

		ApplicationContext securityContext = new ClassPathXmlApplicationContext("classpath*:security.xml");
		byte[] salt = DatatypeConverter.parseHexBinary((String) securityContext.getBean("passwordSaltHexString"));
		SecretKeyFactory secretKeyFactory = (SecretKeyFactory) securityContext.getBean("secretKeyFactory");
		PasswordEncryptor passwordEncryptor = new PasswordEncryptor(salt, secretKeyFactory);

		ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();

		ArrayList<Tenant> tenants = new ArrayList<>();
		ArrayList<User> users = new ArrayList<>();
		ArrayList<Project> projects = new ArrayList<>();
		ArrayList<Workflow> workflows = new ArrayList<>();
		ArrayList<Node> nodes = new ArrayList<>();
		ArrayList<Link> links = new ArrayList<>();

		// Tenant
		{
			for (int i = 0; i < 2; i++) {
				Tenant tenant = new Tenant();
				tenant.name = "Tenant " + (i + 1);
				tenant.fillWithDefaultValues();
				tenants.add(tenant);
			}
		}

		// User
		{
			User user;
			Tenant tenant;

			user = new User();
			users.add(user);
			tenant = tenants.get(threadLocalRandom.nextInt(tenants.size()));
			user.tenantId = tenant.id;
			user.name = "Tester";
			user.setEmail("test@example.com");
			user.setPassword(passwordEncryptor.encrypt("test"));   // Fucking ugly.

			user = new User();
			users.add(user);
			tenant = tenants.get(threadLocalRandom.nextInt(tenants.size()));
			user.tenantId = tenant.id;
			user.name = "Wilson";
			user.setEmail("yyfearth@gmail.com");
			user.setPassword(passwordEncryptor.encrypt("yyfearth"));   // Yuck!

			user = new User();
			users.add(user);
			tenant = tenants.get(threadLocalRandom.nextInt(tenants.size()));
			user.tenantId = tenant.id;
			user.name = "Leo";
			user.setEmail("otaru14204@hotmail.com");
			user.setPassword(passwordEncryptor.encrypt("otaru14204"));   // Damn

			user = new User();
			users.add(user);
			tenant = tenants.get(threadLocalRandom.nextInt(tenants.size()));
			user.tenantId = tenant.id;
			user.name = "Mason";
			user.setEmail("masonwan@gmail.com");
			user.setPassword(passwordEncryptor.encrypt("test"));

			for (User u : users) {
				u.fillWithDefaultValues();
			}
		}

		// Workflow
		{
			for (int i = 1; i <= 5; i++) {
				Tenant tenant = tenants.get(threadLocalRandom.nextInt(tenants.size()));

				Workflow workflow = new Workflow();
				workflows.add(workflow);

				workflow.tenantId = tenant.id;
				workflow.description = StringTool.getRandomString(120);
				ObjectId modifyingUserId = users.get(threadLocalRandom.nextInt(users.size())).id;
				workflow.modifiedByUserId = modifyingUserId;
				ObjectId creatingUserId = users.get(threadLocalRandom.nextInt(users.size())).id;
				workflow.createdByUserId = creatingUserId;

				if (i <= 2) {
					workflow.name = "Alpha";
				} else {
					workflow.name = "Beta";
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(workflow.modifiedDate);
					calendar.add(Calendar.HOUR, threadLocalRandom.nextInt(-12, 13));
					workflow.modifiedDate = calendar.getTime();
				}
				workflow.name += " Workflow " + i;

				// Nodes
				int numNodes = threadLocalRandom.nextInt(2, 8);
				workflow.nodeIdList = new ArrayList<>(numNodes);
				for (int j = 0; j < numNodes; j++) {
					Node node = new Node();
					nodes.add(node);

					node.workflowId = workflow.id;
					node.tenantId = tenant.id;
					node.name = "Node " + (j + 1);
					node.description = StringTool.getRandomString(60);
					node.createdByUserId = creatingUserId;
					node.modifiedByUserId = modifyingUserId;

					node.actions = new ArrayList<>();
					int numActions = threadLocalRandom.nextInt(1, 3);
					for (int k = 0; k < numActions; k++) {
						Action action = new Action();
						node.actions.add(action);
						action.tenantId = tenant.id;
						action.name = "Action " + (k + 1);
						action.createdByUserId = creatingUserId;
						action.modifiedByUserId = modifyingUserId;
						action.content = "Not implemented";
						action.fillWithDefaultValues();
					}

					node.fillWithDefaultValues();
					workflow.nodeIdList.add(node.id);
				}

				workflow.linkIdList = new ArrayList<>();
				for (int j = 0; j < numNodes - 1; j++) {
					Node previousNode = nodes.get(threadLocalRandom.nextInt(nodes.size() - numNodes, nodes.size()));
					Node nextNode = nodes.get(threadLocalRandom.nextInt(nodes.size() - numNodes, nodes.size()));

					Link link = new Link();
					links.add(link);

					link.workflowId = workflow.id;
					link.tenantId = tenant.id;
					link.name = "Link " + (j + 1);
					link.createdByUserId = creatingUserId;
					link.modifiedByUserId = modifyingUserId;
					link.previousNodeId = previousNode.id;
					link.nextNodeId = nextNode.id;
					link.fillWithDefaultValues();
					workflow.linkIdList.add(link.id);

					Condition condition = new Condition();
					condition.tenantId = tenant.id;
					condition.leftOperandType = null;
					condition.leftOperand = null;
					condition.rightOperandType = null;
					condition.rightOperand = null;
					condition.fillWithDefaultValues();
					link.condition = condition;
				}

				workflow.fillWithDefaultValues();
			}
		}

		HashMap<Class, ArrayList> map = new HashMap<>();
		map.put(Tenant.class, tenants);
		map.put(User.class, users);
		map.put(Workflow.class, workflows);
		map.put(Node.class, nodes);
		map.put(Link.class, links);

		for (Class aClass : map.keySet()) {
			mongoTemplate.dropCollection(aClass);
			mongoTemplate.insert(map.get(aClass), aClass);
		}
	}
}
