package marxo.dev;

import com.rits.cloning.Cloner;
import marxo.entity.*;
import marxo.entity.action.*;
import marxo.tool.Loggable;
import marxo.tool.PasswordEncryptor;
import marxo.tool.StringTool;
import org.bson.types.ObjectId;
import org.joda.time.Days;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;

import javax.crypto.SecretKeyFactory;
import javax.xml.bind.DatatypeConverter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@SuppressWarnings("unchecked")
public class AdvancedGenerator extends BasicGenerator implements Loggable {
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
		ArrayList<Workflow> workflows = new ArrayList<>();
		ArrayList<Node> nodes = new ArrayList<>();
		ArrayList<Link> links = new ArrayList<>();
		ArrayList<Context> contexts = new ArrayList<>();
		ArrayList<Notification> notifications = new ArrayList<>();

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
				workflow.name += " Workflow " + i;
				workflow.fillWithDefaultValues();
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
					workflow.modifiedDate.plusHours(threadLocalRandom.nextInt(-12, 13));
				}

				// Nodes
				int numNodes = threadLocalRandom.nextInt(2, 8);
				workflow.nodeIds = new ArrayList<>(numNodes);
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
						action.fillWithDefaultValues();
					}

					node.fillWithDefaultValues();
					workflow.nodeIds.add(node.id);
				}

				workflow.linkIds = new ArrayList<>();
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
					workflow.linkIds.add(link.id);

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

			// a complete example case.
			{
				List<TenantChildEntity> tenantChildEntities = new ArrayList<>();

				Workflow workflow = new Workflow();
				workflows.add(workflow);
				tenantChildEntities.add(workflow);
				workflow.name = "Mobile app development";
				workflow.fillWithDefaultValues();
				workflow.description = "Develop a mobile for student project.";
				workflow.createdByUserId = workflow.modifiedByUserId = users.get(users.size() - 1).id;

				Node node;
				Action action;
				Link linkLeft, linkRight;
				Condition condition;
				FacebookPost facebookPost;
				Schedule schedule;
				Trigger trigger;

				node = new Node();
				tenantChildEntities.add(node);
				node.name = "Make the requirement";
				node.fillWithDefaultValues();

				action = new Action();
				node.actions.add(action);
				action.name = "Announce the requirement";
				action.contextType = ContextType.POST_FACEBOOK;

				facebookPost = new FacebookPost();
				facebookPost.content = "This is requirement.";
				facebookPost.actionId = action.id;
				facebookPost.fillWithDefaultValues();
				action.contextId = facebookPost.id;
				contexts.add(facebookPost);

				action = new Action();
				node.actions.add(action);
				action.name = "Wait for the submissions";
				action.contextType = ContextType.SCHEDULE_EVENT;

				schedule = new Schedule();
				schedule.duration = Days.days(1).toStandardDuration();
				schedule.fillWithDefaultValues();
				action.contextId = schedule.id;
				contexts.add(schedule);

				action = new Action();
				node.actions.add(action);
				action.name = "Open for voting";
				action.contextType = ContextType.POST_FACEBOOK;

				facebookPost = new FacebookPost();
				facebookPost.content = "Please vote the design.";
				facebookPost.actionId = action.id;
				facebookPost.fillWithDefaultValues();
				action.contextId = facebookPost.id;
				contexts.add(facebookPost);

				action = new Action();
				node.actions.add(action);
				action.name = "Wait for the votes";
				action.contextType = ContextType.SCHEDULE_EVENT;

				schedule = new Schedule();
				schedule.duration = Days.days(1).toStandardDuration();
				schedule.fillWithDefaultValues();
				action.contextId = schedule.id;
				contexts.add(schedule);

				linkLeft = new Link();
				tenantChildEntities.add(linkLeft);
				linkLeft.name = "To client development";
				linkLeft.fillWithDefaultValues();
				linkLeft.previousNodeId = node.id;

				condition = new Condition();
				tenantChildEntities.add(condition);
				linkLeft.condition = condition;

				linkRight = new Link();
				tenantChildEntities.add(linkRight);
				linkRight.name = "To server development";
				linkRight.fillWithDefaultValues();
				linkRight.previousNodeId = node.id;

				condition = new Condition();
				tenantChildEntities.add(condition);
				linkRight.condition = condition;

				node = new Node();
				tenantChildEntities.add(node);
				node.name = "Develop the client";
				node.fillWithDefaultValues();
				linkLeft.nextNodeId = node.id;

				action = new Action();
				node.actions.add(action);
				action.name = "Announce the client requirement";
				action.contextType = ContextType.POST_FACEBOOK;

				facebookPost = new FacebookPost();
				facebookPost.content = "Please design the client";
				facebookPost.actionId = action.id;
				facebookPost.fillWithDefaultValues();
				action.contextId = facebookPost.id;
				contexts.add(facebookPost);

				action = new Action();
				node.actions.add(action);
				action.name = "Wait for the client submissions";
				action.contextType = ContextType.SCHEDULE_EVENT;

				schedule = new Schedule();
				schedule.duration = Days.days(1).toStandardDuration();
				schedule.fillWithDefaultValues();
				action.contextId = schedule.id;
				contexts.add(schedule);

				action = new Action();
				node.actions.add(action);
				action.name = "Wait for user review";
				action.contextType = ContextType.TRIGGER;

				trigger = new Trigger();
				trigger.fillWithDefaultValues();
				action.contextId = trigger.id;
				contexts.add(trigger);

				linkLeft = new Link();
				tenantChildEntities.add(linkLeft);
				linkLeft.name = "Client to close";
				linkLeft.fillWithDefaultValues();
				linkLeft.previousNodeId = node.id;

				condition = new Condition();
				tenantChildEntities.add(condition);
				linkLeft.condition = condition;

				node = new Node();
				tenantChildEntities.add(node);
				node.name = "Develop the server";
				node.fillWithDefaultValues();
				linkRight.nextNodeId = node.id;

				action = new Action();
				node.actions.add(action);
				action.name = "Announce the server requirement";
				action.contextType = ContextType.POST_FACEBOOK;

				facebookPost = new FacebookPost();
				facebookPost.content = "Please design the server";
				facebookPost.actionId = action.id;
				facebookPost.fillWithDefaultValues();
				action.contextId = facebookPost.id;
				contexts.add(facebookPost);

				action = new Action();
				node.actions.add(action);
				action.name = "Wait for the server submissions";
				action.contextType = ContextType.SCHEDULE_EVENT;

				schedule = new Schedule();
				schedule.duration = Days.days(1).toStandardDuration();
				schedule.fillWithDefaultValues();
				action.contextId = schedule.id;
				contexts.add(schedule);

				action = new Action();
				node.actions.add(action);
				action.name = "Wait for user review";
				action.contextType = ContextType.TRIGGER;

				trigger = new Trigger();
				trigger.fillWithDefaultValues();
				action.contextId = trigger.id;
				contexts.add(trigger);

				linkRight = new Link();
				tenantChildEntities.add(linkRight);
				linkRight.name = "To server development";
				linkRight.fillWithDefaultValues();
				linkRight.previousNodeId = node.id;

				condition = new Condition();
				tenantChildEntities.add(condition);
				linkRight.condition = condition;

				node = new Node();
				tenantChildEntities.add(node);
				node.name = "Announce the result";
				node.fillWithDefaultValues();
				linkLeft.nextNodeId = node.id;
				linkRight.nextNodeId = node.id;

				action = new Action();
				node.actions.add(action);
				action.name = "Post result to Facebook";
				action.contextType = ContextType.POST_FACEBOOK;

				facebookPost = new FacebookPost();
				facebookPost.content = "Please design the client";
				facebookPost.actionId = action.id;
				facebookPost.fillWithDefaultValues();
				action.contextId = facebookPost.id;
				contexts.add(facebookPost);

				Tenant tenant = tenants.get(0);
				for (TenantChildEntity tenantChildEntity : tenantChildEntities) {
					tenantChildEntity.tenantId = tenant.id;

					if (tenantChildEntity instanceof Node) {
						node = (Node) tenantChildEntity;
						node.workflowId = workflow.id;
						nodes.add(node);
						workflow.nodeIds.add(node.id);
					} else if (tenantChildEntity instanceof Link) {
						Link link = (Link) tenantChildEntity;
						link.workflowId = workflow.id;
						links.add(link);
						workflow.linkIds.add(link.id);
					}
				}
			}
		}

		// Project
		{
			Cloner cloner = new Cloner();

			HashMap<ObjectId, Node> nodeMap = new HashMap<>();
			for (Node node : nodes) {
				nodeMap.put(node.id, node);
			}

			HashMap<ObjectId, Link> linkMap = new HashMap<>();
			for (Link link : links) {
				linkMap.put(link.id, link);
			}

			HashMap<ObjectId, Context> contextMap = new HashMap<>();
			for (Context context : contexts) {
				contextMap.put(context.id, context);
			}

			for (int i = 0; i < 2; i++) {
				Workflow workflow = workflows.get(i);
				Workflow project = cloner.deepClone(workflow);

				project.id = new ObjectId();
				project.templateId = workflow.id;
				project.isProject = true;

				ArrayList<ObjectId> newNodeIds = new ArrayList<>();
				for (ObjectId nodeId : project.nodeIds) {
					Node node = nodeMap.get(nodeId);
					Node newNode = cloner.deepClone(node);
					newNode.id = new ObjectId();
					newNode.workflowId = project.id;
					nodes.add(newNode);
					newNodeIds.add(newNode.id);

					for (Action action : newNode.actions) {
						action.id = new ObjectId();
					}
				}
				project.nodeIds = newNodeIds;

				ArrayList<ObjectId> newLinkIds = new ArrayList<>();
				for (ObjectId linkId : project.linkIds) {
					Link link = linkMap.get(linkId);
					Link newLink = cloner.deepClone(link);
					newLink.id = new ObjectId();
					newLink.workflowId = project.id;
					newLink.templateId = link.id;
					links.add(newLink);
					newLinkIds.add(newLink.id);

					newLink.condition.id = new ObjectId();
				}
				project.linkIds = newLinkIds;

				workflows.add(project);
			}
		}

		HashMap<Class, ArrayList> map = new HashMap<>();
		map.put(Tenant.class, tenants);
		map.put(User.class, users);
		map.put(Workflow.class, workflows);
		map.put(Node.class, nodes);
		map.put(Link.class, links);
		map.put(Context.class, contexts);
		map.put(Notification.class, notifications);

		for (Class aClass : map.keySet()) {
			mongoTemplate.dropCollection(aClass);
			mongoTemplate.insert(map.get(aClass), aClass);
		}
	}
}
