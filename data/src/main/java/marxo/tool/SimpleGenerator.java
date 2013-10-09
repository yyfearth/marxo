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
import java.util.Random;

/**
 * It generates entities without the relationship.
 */
public class SimpleGenerator extends BasicGenerator {
	public static void main(String[] args) {
		final Logger logger = LoggerFactory.getLogger(SimpleGenerator.class);

		ApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"mongo-configuration.xml"});
		MongoTemplate mongoTemplate = context.getBean(MongoTemplate.class);

		Random random = new Random();

		ArrayList<Tenant> tenants = new ArrayList<>();
		ArrayList<User> users = new ArrayList<>();
		ArrayList<Project> projects = new ArrayList<>();
		ArrayList<Workflow> workflows = new ArrayList<>();
		ArrayList<Node> nodes = new ArrayList<>();
		ArrayList<Action> actions = new ArrayList<>();
		ArrayList<Link> links = new ArrayList<>();
		ArrayList<Condition> conditions = new ArrayList<>();

		// Tenant
		{
			for (int i = 1; i <= 10; i++) {
				Tenant t = new Tenant();
				t.name = "Tenant " + i;
				t.fillWithDefaultValues();
				tenants.add(t);
			}
		}

		// User
		{
			for (int i = 1; i <= 10; i++) {
				User u = new User(new ObjectId(), getRandomHumanName());
				u.fillWithDefaultValues();
				users.add(u);
			}
		}

		// Workflow
		{
			for (int i = 1; i <= 3; i++) {
				Workflow w = new Workflow();
				w.name = "Workflow " + i;
				w.description = RandomStringUtils.randomAlphabetic(10);
				w.fillWithDefaultValues();
				workflows.add(w);

				ObjectId modifiedBy = users.get(random.nextInt(users.size())).id;
				w.modifiedByUserId = modifiedBy;
				ObjectId createdBy = users.get(random.nextInt(users.size())).id;
				w.createdByUserId = createdBy;
			}
		}

		// Project
		{
			for (int i = 1; i <= 10; i++) {
				Tenant tenant = tenants.get(random.nextInt(tenants.size()));
				Workflow workflow = workflows.get(random.nextInt(workflows.size()));

				Project p = new Project();
				p.tenantId = tenant.id;
				p.name = "Project " + i;
				p.description = RandomStringUtils.randomAlphanumeric(10);
				p.fillWithDefaultValues();
				projects.add(p);

				boolean hasTemplate = random.nextBoolean();
				if (hasTemplate) {
					p.templateId = workflow.id;
					p.modifiedByUserId = workflow.modifiedByUserId;
					p.createdByUserId = workflow.createdByUserId;
				} else {
					ObjectId modifiedBy = users.get(random.nextInt(users.size())).id;
					p.modifiedByUserId = modifiedBy;
					ObjectId createdBy = users.get(random.nextInt(users.size())).id;
					p.createdByUserId = createdBy;
				}
			}
		}

		// Node
		{
			for (int i = 1; i <= 10; i++) {
				Tenant tenant = tenants.get(random.nextInt(tenants.size()));

				Node node = new Node();
				node.name = "Node " + i;
				node.description = getRandomProjectName();
				node.fillWithDefaultValues();
				nodes.add(node);

				ObjectId modifiedBy = users.get(random.nextInt(users.size())).id;
				node.modifiedByUserId = modifiedBy;
				ObjectId createdBy = users.get(random.nextInt(users.size())).id;
				node.createdByUserId = createdBy;

				int workflowIndex = random.nextInt(workflows.size());
				Workflow workflow = workflows.get(workflowIndex);
				workflow.nodeIdList.add(node.id);
				node.workflowId = workflows.get(workflowIndex).id;

				int actionNum = random.nextInt(4);
				for (int j = 0; j < actionNum; j++) {
					Action action = new Action();
					action.name = "Action " + j;
					action.type = "post_to_multi_social_media";
					action.content = "....";
					action.fillWithDefaultValues();
					actions.add(action);
					node.actions.add(action);
				}
			}
		}

		// Link
		{
			for (int i = 1; i <= 10; i++) {
				Link link = new Link();
				link.id = new ObjectId();
				link.title = "Link " + i;
				link.name = getRandomProjectName();
				link.fillWithDefaultValues();
				links.add(link);

				ObjectId modifiedBy = users.get(random.nextInt(users.size())).id;
				link.modifiedByUserId = modifiedBy;
				ObjectId createdBy = users.get(random.nextInt(users.size())).id;
				link.createdByUserId = createdBy;

				boolean hasCondition = random.nextBoolean();
				if (hasCondition) {
					Condition condition = new Condition();
					condition.name = "Condition " + conditions.size();
					condition.description = "Cancel if like count < 300";
					condition.leftOperand = "like.account";
					condition.leftOperandType = "data.number";
					condition.rightOperand = "300";
					condition.rightOperandType = "number";
					condition.operator = "<";
					condition.fillWithDefaultValues();
					conditions.add(condition);
					link.condition = condition;

					condition.modifiedByUserId = modifiedBy;
					condition.createdByUserId = createdBy;
				}

				int workflowIndex = random.nextInt(workflows.size());
				Workflow workflow = workflows.get(workflowIndex);
				workflow.linkIdList.add(link.id);
				link.workflowId = workflow.id;
				link.previousNodeId = nodes.get(random.nextInt(nodes.size())).id;
				link.nextNodeId = nodes.get(random.nextInt(nodes.size())).id;
			}
		}

		mongoTemplate.dropCollection(Tenant.class);
		mongoTemplate.insert(tenants, Tenant.class);
		mongoTemplate.dropCollection(User.class);
		mongoTemplate.insert(users, User.class);
		mongoTemplate.dropCollection(Workflow.class);
		mongoTemplate.insert(workflows, Workflow.class);
		mongoTemplate.dropCollection(Project.class);
		mongoTemplate.insert(projects, Project.class);
		mongoTemplate.dropCollection(Node.class);
		mongoTemplate.insert(nodes, Node.class);
		mongoTemplate.dropCollection(Action.class);
		mongoTemplate.insert(actions, Action.class);
		mongoTemplate.dropCollection(Link.class);
		mongoTemplate.insert(links, Link.class);
	}

//	static <T extends BasicEntity> ArrayList<T> getList(Class<T> tClass, int number) {
//		ArrayList<T> list = new ArrayList<>(number);
//		return list;
//	}
}
