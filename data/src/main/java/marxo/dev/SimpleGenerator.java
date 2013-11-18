package marxo.dev;

import marxo.entity.link.Condition;
import marxo.entity.link.Link;
import marxo.entity.node.Action;
import marxo.entity.node.Node;
import marxo.entity.user.Tenant;
import marxo.entity.user.User;
import marxo.entity.workflow.Workflow;
import marxo.tool.Loggable;
import marxo.tool.StringTool;
import org.bson.types.ObjectId;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.ArrayList;
import java.util.Random;

/**
 * It generates entities without the relationship.
 */
public class SimpleGenerator extends BasicGenerator implements Loggable {
	public static void main(String[] args) {
//		ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:marxo/mongo-configuration.xml");
//		MongoTemplate mongoTemplate = context.getBean(MongoTemplate.class);
//
//		Random random = new Random();
//
//		ArrayList<Tenant> tenants = new ArrayList<>();
//		ArrayList<User> users = new ArrayList<>();
//		ArrayList<Workflow> workflows = new ArrayList<>();
//		ArrayList<Node> nodes = new ArrayList<>();
//		ArrayList<Action> actions = new ArrayList<>();
//		ArrayList<Link> links = new ArrayList<>();
//		ArrayList<Condition> conditions = new ArrayList<>();
//
//		// Tenant
//		{
//			for (int i = 1; i <= 10; i++) {
//				Tenant t = new Tenant();
//				t.setName("Tenant " + i);
//				t.fillWithDefaultValues();
//				tenants.add(t);
//			}
//		}
//
//		// User
//		{
//			for (int i = 1; i <= 10; i++) {
//				User u = new User();
//				u.setName(getRandomHumanName());
//				u.fillWithDefaultValues();
//				users.add(u);
//			}
//		}
//
//		// Workflow
//		{
//			for (int i = 1; i <= 3; i++) {
//				Workflow w = new Workflow();
//				w.setName("Workflow " + i);
//				w.description = StringTool.getRandomString(120);
//				w.fillWithDefaultValues();
//				workflows.add(w);
//
//				ObjectId modifiedBy = users.get(random.nextInt(users.size())).id;
//				w.modifiedByUserId = modifiedBy;
//				ObjectId createdBy = users.get(random.nextInt(users.size())).id;
//				w.createdByUserId = createdBy;
//			}
//		}
//
//		// Node
//		{
//			for (int i = 1; i <= 10; i++) {
//				Tenant tenant = tenants.get(random.nextInt(tenants.size()));
//
//				Node node = new Node();
//				node.setName("Node " + i);
//				node.description = StringTool.getRandomString(120);
//				node.fillWithDefaultValues();
//				nodes.add(node);
//
//				ObjectId modifiedBy = users.get(random.nextInt(users.size())).id;
//				node.modifiedByUserId = modifiedBy;
//				ObjectId createdBy = users.get(random.nextInt(users.size())).id;
//				node.createdByUserId = createdBy;
//
//				int workflowIndex = random.nextInt(workflows.size());
//				Workflow workflow = workflows.get(workflowIndex);
//				workflow.nodeIds.add(node.id);
//
//				int actionNum = random.nextInt(4);
//				for (int j = 0; j < actionNum; j++) {
//					Action action = new Action();
//					action.setName("Action " + j);
//					action.fillWithDefaultValues();
//					actions.add(action);
//					node.actions.add(action);
//				}
//			}
//		}
//
//		// Link
//		{
//			for (int i = 1; i <= 10; i++) {
//				Link link = new Link();
//				link.id = new ObjectId();
//				link.setName("Link " + i);
//				link.fillWithDefaultValues();
//				links.add(link);
//
//				ObjectId modifiedBy = users.get(random.nextInt(users.size())).id;
//				link.modifiedByUserId = modifiedBy;
//				ObjectId createdBy = users.get(random.nextInt(users.size())).id;
//				link.createdByUserId = createdBy;
//
//				boolean hasCondition = random.nextBoolean();
//				if (hasCondition) {
//					Condition condition = new Condition();
//					condition.setName("Condition " + conditions.size());
//					condition.description = "Cancel if like count < 300";
//					condition.leftOperand = "like.account";
//					condition.leftOperandType = "data.number";
//					condition.rightOperand = "300";
//					condition.rightOperandType = "number";
//					condition.operator = "<";
//					condition.fillWithDefaultValues();
//					conditions.add(condition);
//					link.condition = condition;
//
//					condition.modifiedByUserId = modifiedBy;
//					condition.createdByUserId = createdBy;
//				}
//
//				int workflowIndex = random.nextInt(workflows.size());
//				Workflow workflow = workflows.get(workflowIndex);
//				workflow.linkIds.add(link.id);
//				link.workflowId = workflow.id;
//				link.previousNodeId = nodes.get(random.nextInt(nodes.size())).id;
//				link.nextNodeId = nodes.get(random.nextInt(nodes.size())).id;
//			}
//		}
//
//		mongoTemplate.dropCollection(Tenant.class);
//		mongoTemplate.insert(tenants, Tenant.class);
//		mongoTemplate.dropCollection(User.class);
//		mongoTemplate.insert(users, User.class);
//		mongoTemplate.dropCollection(Workflow.class);
//		mongoTemplate.insert(workflows, Workflow.class);
//		mongoTemplate.dropCollection(Node.class);
//		mongoTemplate.insert(nodes, Node.class);
//		mongoTemplate.dropCollection(Action.class);
//		mongoTemplate.insert(actions, Action.class);
//		mongoTemplate.dropCollection(Link.class);
//		mongoTemplate.insert(links, Link.class);
	}
}
