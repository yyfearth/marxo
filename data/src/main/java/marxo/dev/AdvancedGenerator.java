package marxo.dev;

import com.google.common.collect.Maps;
import com.rits.cloning.Cloner;
import marxo.entity.Notification;
import marxo.entity.link.Condition;
import marxo.entity.link.Link;
import marxo.entity.node.Action;
import marxo.entity.node.Node;
import marxo.entity.node.PostFacebookAction;
import marxo.entity.user.Tenant;
import marxo.entity.user.User;
import marxo.entity.workflow.Workflow;
import marxo.tool.Loggable;
import marxo.tool.PasswordEncryptor;
import marxo.tool.StringTool;
import marxo.validation.SelectIdFunction;
import org.bson.types.ObjectId;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;

import javax.crypto.SecretKeyFactory;
import javax.xml.bind.DatatypeConverter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
		ArrayList<Notification> notifications = new ArrayList<>();

		// Tenant
		{
			for (int i = 0; i < 2; i++) {
				Tenant tenant = new Tenant();
				tenant.setName("Tenant " + (i + 1));
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
			user.setName("Tester");
			user.setEmail("test@example.com");
			user.setPassword(passwordEncryptor.encrypt("B4driGpKjDrtdKaAoA8nUmm+D2Pl3kxoF5POX0sGSk4"));   // Fucking ugly.

			user = new User();
			users.add(user);
			tenant = tenants.get(0);
			user.tenantId = tenant.id;
			user.setName("Wilson");
			user.setEmail("yyfearth@gmail.com");
			user.setPassword(passwordEncryptor.encrypt("2k96H29ECsJ05BJAkEGm6FC+UgjwVTc1qOd7SGG2uS8"));   // Yuck!

			user = new User();
			users.add(user);
			tenant = tenants.get(1);
			user.tenantId = tenant.id;
			user.setName("Leo");
			user.setEmail("otaru14204@hotmail.com");
			user.setPassword(passwordEncryptor.encrypt("XELXdnuv/p7QeCzPM7Pl7TLfd6o2NZSaPb/sGtYUg5Q"));   // Damn

			user = new User();
			users.add(user);
			tenant = tenants.get(1);
			user.tenantId = tenant.id;
			user.setName("Mason");
			user.setEmail("masonwan@gmail.com");
			user.setPassword(passwordEncryptor.encrypt("test"));
		}

		// Workflow
		{
			for (int i = 1; i <= 5; i++) {
				Workflow workflow = new Workflow();
				workflows.add(workflow);

				workflow.description = StringTool.getRandomString(120);
				User user;

				if (i <= 3) {
					user = users.get(1);
				} else {
					user = users.get(3);
				}

				ObjectId modifyingUserId = user.id;
				workflow.updateUserId = modifyingUserId;
				ObjectId creatingUserId = user.id;
				workflow.createUserId = creatingUserId;
				workflow.tenantId = user.tenantId;

				if (i <= 2) {
					workflow.setName("Alpha " + "Workflow " + i);
				} else {
					workflow.setName("Beta " + "Workflow " + i);
					workflow.updateTime.plusHours(threadLocalRandom.nextInt(-12, 13));
				}

				// Nodes
				int numNodes = threadLocalRandom.nextInt(2, 8);
				for (int j = 0; j < numNodes; j++) {
					Node node = new Node();
					nodes.add(node);

					node.workflowId = workflow.id;
					node.tenantId = user.tenantId;
					node.setName("Node " + (j + 1));
					node.description = StringTool.getRandomString(60);
					node.createUserId = creatingUserId;
					node.updateUserId = modifyingUserId;

					node.setActions(new ArrayList<Action>());
					int numActions = threadLocalRandom.nextInt(1, 3);
					for (int k = 0; k < numActions; k++) {
						Action action = new PostFacebookAction();
						node.getActions().add(action);
						action.tenantId = user.tenantId;
						action.setName("Action " + (k + 1));
						action.createUserId = creatingUserId;
						action.updateUserId = modifyingUserId;
					}

					workflow.nodeIds.add(node.id);
				}

				for (int j = 0; j < numNodes - 1; j++) {
					Node previousNode = nodes.get(threadLocalRandom.nextInt(nodes.size() - numNodes, nodes.size()));
					Node nextNode = nodes.get(threadLocalRandom.nextInt(nodes.size() - numNodes, nodes.size()));

					Link link = new Link();
					links.add(link);

					link.workflowId = workflow.id;
					link.tenantId = user.tenantId;
					link.setName("Link " + (j + 1));
					link.createUserId = creatingUserId;
					link.updateUserId = modifyingUserId;
					link.previousNodeId = previousNode.id;
					link.nextNodeId = nextNode.id;
					workflow.linkIds.add(link.id);

					Condition condition = new Condition();
					condition.tenantId = user.tenantId;
					condition.leftOperandType = null;
					condition.leftOperand = null;
					condition.rightOperandType = null;
					condition.rightOperand = null;
					link.condition = condition;
				}
			}

			// a complete example case.
//			{
//				List<TenantChildEntity> tenantChildEntities = new ArrayList<>();
//
//				Workflow workflow = new Workflow();
//				workflows.add(workflow);
//				tenantChildEntities.add(workflow);
//				workflow.setName("Mobile app development");
//				workflow.fillWithDefaultValues();
//				workflow.description = "Develop a mobile for student project.";
//				workflow.createUserId = workflow.updateUserId = users.get(users.size() - 1).id;
//
//				Node node;
//				Action action;
//				Link linkLeft, linkRight;
//				Condition condition;
//				PostFacebookAction facebookPost;
//				Event event;
//				Trigger trigger;
//
//				node = new Node();
//				tenantChildEntities.add(node);
//				node.setName("Make the requirement");
//				node.fillWithDefaultValues();
//
//				facebookPost = new PostFacebookAction();
//				facebookPost.setName("Announce the requirement");
//				facebookPost.content = "This is requirement.";
//				facebookPost.actionId = action.id;
//				facebookPost.fillWithDefaultValues();
//				action.contentId = facebookPost.id;
//
//
//
//				action = new Action();
//				node.actions.add(action);
//				action.setName("Wait for the submissions");
//				action.contextType = ContextType.SCHEDULE_EVENT;
//
//				event = new Event();
//				event.duration = Days.days(1).toStandardDuration();
//				event.fillWithDefaultValues();
//				action.contentId = event.id;
//
//
//				action = new Action();
//				node.actions.add(action);
//				action.setName("Open for voting");
//				action.contextType = ContextType.POST_FACEBOOK;
//
//				facebookPost = new PostFacebookAction();
//				facebookPost.content = "Please vote the design.";
//				facebookPost.actionId = action.id;
//				facebookPost.fillWithDefaultValues();
//				action.contentId = facebookPost.id;
//
//
//				action = new Action();
//				node.actions.add(action);
//				action.setName("Wait for the votes");
//				action.contextType = ContextType.SCHEDULE_EVENT;
//
//				event = new Event();
//				event.duration = Days.days(1).toStandardDuration();
//				event.fillWithDefaultValues();
//				action.contentId = event.id;
//
//
//				linkLeft = new Link();
//				tenantChildEntities.add(linkLeft);
//				linkLeft.setName("To client development");
//				linkLeft.fillWithDefaultValues();
//				linkLeft.previousNodeId = node.id;
//
//				condition = new Condition();
//				tenantChildEntities.add(condition);
//				linkLeft.condition = condition;
//
//				linkRight = new Link();
//				tenantChildEntities.add(linkRight);
//				linkRight.setName("To server development");
//				linkRight.fillWithDefaultValues();
//				linkRight.previousNodeId = node.id;
//
//				condition = new Condition();
//				tenantChildEntities.add(condition);
//				linkRight.condition = condition;
//
//				node = new Node();
//				tenantChildEntities.add(node);
//				node.setName("Develop the client");
//				node.fillWithDefaultValues();
//				linkLeft.nextNodeId = node.id;
//
//				action = new Action();
//				node.actions.add(action);
//				action.setName("Announce the client requirement");
//
//				facebookPost = new PostFacebookAction();
//				facebookPost.content = "Please design the client";
//				facebookPost.actionId = action.id;
//				facebookPost.fillWithDefaultValues();
//				action.contentId = facebookPost.id;
//
//
//				action = new Action();
//				node.actions.add(action);
//				action.setName("Wait for the client submissions");
//				action.contextType = ContextType.SCHEDULE_EVENT;
//
//				event = new Event();
//				event.duration = Days.days(1).toStandardDuration();
//				event.fillWithDefaultValues();
//				action.contentId = event.id;
//
//
//				action = new Action();
//				node.actions.add(action);
//				action.setName("Wait for user review");
//				action.contextType = ContextType.TRIGGER;
//
//				trigger = new Trigger();
//				trigger.fillWithDefaultValues();
//				action.contentId = trigger.id;
//
//
//				linkLeft = new Link();
//				tenantChildEntities.add(linkLeft);
//				linkLeft.setName("Client to close");
//				linkLeft.fillWithDefaultValues();
//				linkLeft.previousNodeId = node.id;
//
//				condition = new Condition();
//				tenantChildEntities.add(condition);
//				linkLeft.condition = condition;
//
//				node = new Node();
//				tenantChildEntities.add(node);
//				node.setName("Develop the server");
//				node.fillWithDefaultValues();
//				linkRight.nextNodeId = node.id;
//
//				action = new Action();
//				node.actions.add(action);
//				action.setName("Announce the server requirement");
//				action.contextType = ContextType.POST_FACEBOOK;
//
//				facebookPost = new PostFacebookAction();
//				facebookPost.content = "Please design the server";
//				facebookPost.actionId = action.id;
//				facebookPost.fillWithDefaultValues();
//				action.contentId = facebookPost.id;
//
//
//				action = new Action();
//				node.actions.add(action);
//				action.setName("Wait for the server submissions");
//				action.contextType = ContextType.SCHEDULE_EVENT;
//
//				event = new Event();
//				event.duration = Days.days(1).toStandardDuration();
//				event.fillWithDefaultValues();
//				action.contentId = event.id;
//
//
//				action = new Action();
//				node.actions.add(action);
//				action.setName("Wait for user review");
//				action.contextType = ContextType.TRIGGER;
//
//				trigger = new Trigger();
//				trigger.fillWithDefaultValues();
//				action.contentId = trigger.id;
//
//
//				linkRight = new Link();
//				tenantChildEntities.add(linkRight);
//				linkRight.setName("To server development");
//				linkRight.fillWithDefaultValues();
//				linkRight.previousNodeId = node.id;
//
//				condition = new Condition();
//				tenantChildEntities.add(condition);
//				linkRight.condition = condition;
//
//				node = new Node();
//				tenantChildEntities.add(node);
//				node.setName("Announce the result");
//				node.fillWithDefaultValues();
//				linkLeft.nextNodeId = node.id;
//				linkRight.nextNodeId = node.id;
//
//				action = new Action();
//				node.actions.add(action);
//				action.setName("Post result to Facebook");
//				action.contextType = ContextType.POST_FACEBOOK;
//
//				facebookPost = new PostFacebookAction();
//				facebookPost.content = "Please design the client";
//				facebookPost.actionId = action.id;
//				facebookPost.fillWithDefaultValues();
//				action.contentId = facebookPost.id;
//
//
//				Tenant tenant = tenants.get(0);
//				for (TenantChildEntity tenantChildEntity : tenantChildEntities) {
//					tenantChildEntity.tenantId = tenant.id;
//
//					if (tenantChildEntity instanceof Node) {
//						node = (Node) tenantChildEntity;
//						node.workflowId = workflow.id;
//						nodes.add(node);
//						workflow.nodeIds.add(node.id);
//					} else if (tenantChildEntity instanceof Link) {
//						Link link = (Link) tenantChildEntity;
//						link.workflowId = workflow.id;
//						links.add(link);
//						workflow.linkIds.add(link.id);
//					}
//				}
//			}
//		}

			// Project
			{
				Cloner cloner = new Cloner();

				Map<ObjectId, Node> nodeMap = Maps.uniqueIndex(nodes, SelectIdFunction.getInstance());
				Map<ObjectId, Link> linkMap = Maps.uniqueIndex(links, SelectIdFunction.getInstance());

				for (int i = 0; i < 2; i++) {
					Workflow workflow = workflows.get(i);
					Workflow project = cloner.deepClone(workflow);

					project.id = new ObjectId();
					project.setTemplate(workflow);
					project.isProject = true;

					List<ObjectId> newNodeIds = new ArrayList<>();
					for (ObjectId nodeId : project.nodeIds) {
						Node node = nodeMap.get(nodeId);
						Node newNode = cloner.deepClone(node);
						newNode.id = new ObjectId();
						newNode.workflowId = project.id;
						nodes.add(newNode);
						newNodeIds.add(newNode.id);

						for (Action action : newNode.getActions()) {
							action.id = new ObjectId();
						}
					}
					project.nodeIds = newNodeIds;

					List<ObjectId> newLinkIds = new ArrayList<>();
					for (ObjectId linkId : project.linkIds) {
						Link link = linkMap.get(linkId);
						Link newLink = cloner.deepClone(link);
						newLink.id = new ObjectId();
						newLink.workflowId = project.id;
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
			map.put(Notification.class, notifications);

			for (Class aClass : map.keySet()) {
				mongoTemplate.dropCollection(aClass);
				mongoTemplate.insert(map.get(aClass), aClass);
			}
		}
	}
}
