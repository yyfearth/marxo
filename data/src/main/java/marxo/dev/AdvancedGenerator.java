package marxo.dev;

import marxo.entity.*;
import marxo.tool.ILoggable;
import marxo.tool.PasswordEncryptor;
import marxo.tool.StringTool;
import org.bson.types.ObjectId;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;

import javax.crypto.SecretKeyFactory;
import javax.xml.bind.DatatypeConverter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

public class AdvancedGenerator extends BasicGenerator implements ILoggable {
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
			tenant = tenants.get(0);
			user.tenantId = tenant.id;
			user.name = "Tester";
			user.setEmail("test@example.com");
			user.setPassword(passwordEncryptor.encrypt("B4driGpKjDrtdKaAoA8nUmm+D2Pl3kxoF5POX0sGSk4"));   // Fucking ugly.

			user = new User();
			users.add(user);
			tenant = tenants.get(0);
			user.tenantId = tenant.id;
			user.name = "Wilson";
			user.setEmail("yyfearth@gmail.com");
			user.setPassword(passwordEncryptor.encrypt("2k96H29ECsJ05BJAkEGm6FC+UgjwVTc1qOd7SGG2uS8"));   // Yuck!

			user = new User();
			users.add(user);
			tenant = tenants.get(1);
			user.tenantId = tenant.id;
			user.name = "Leo";
			user.setEmail("otaru14204@hotmail.com");
			user.setPassword(passwordEncryptor.encrypt("XELXdnuv/p7QeCzPM7Pl7TLfd6o2NZSaPb/sGtYUg5Q"));   // Damn

			user = new User();
			users.add(user);
			tenant = tenants.get(1);
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
				Workflow workflow = new Workflow();
				workflows.add(workflow);

				workflow.description = StringTool.getRandomString(120);
				User user = users.get(threadLocalRandom.nextInt(users.size()));
				ObjectId modifyingUserId = user.id;
				workflow.modifiedByUserId = modifyingUserId;
				ObjectId creatingUserId = user.id;
				workflow.createdByUserId = creatingUserId;
				workflow.tenantId = user.tenantId;

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

				workflow.fillWithDefaultValues();

				// Nodes
				int numNodes = threadLocalRandom.nextInt(2, 8);
				workflow.nodeIdList = new ArrayList<>(numNodes);
				for (int j = 0; j < numNodes; j++) {
					Node node = new Node();
					nodes.add(node);

					node.workflowId = workflow.id;
					node.tenantId = user.tenantId;
					node.name = "Node " + (j + 1);
					node.description = StringTool.getRandomString(60);
					node.createdByUserId = creatingUserId;
					node.modifiedByUserId = modifyingUserId;

					node.actions = new ArrayList<>();
					int numActions = threadLocalRandom.nextInt(1, 3);
					for (int k = 0; k < numActions; k++) {
						Action action = new Action();
						node.actions.add(action);
						action.tenantId = user.tenantId;
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
					link.tenantId = user.tenantId;
					link.name = "Link " + (j + 1);
					link.createdByUserId = creatingUserId;
					link.modifiedByUserId = modifyingUserId;
					link.previousNodeId = previousNode.id;
					link.nextNodeId = nextNode.id;
					link.fillWithDefaultValues();
					workflow.linkIdList.add(link.id);

					Condition condition = new Condition();
					condition.tenantId = user.tenantId;
					condition.leftOperandType = null;
					condition.leftOperand = null;
					condition.rightOperandType = null;
					condition.rightOperand = null;
					condition.fillWithDefaultValues();
					link.condition = condition;
				}
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
