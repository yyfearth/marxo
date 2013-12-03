package marxo;

import com.google.common.collect.Lists;
import com.mongodb.DB;
import com.rits.cloning.Cloner;
import marxo.entity.BasicEntity;
import marxo.entity.FacebookData;
import marxo.entity.action.Action;
import marxo.entity.action.MonitorFacebookAction;
import marxo.entity.action.PostFacebookAction;
import marxo.entity.content.Content;
import marxo.entity.content.FacebookContent;
import marxo.entity.content.FacebookMonitorContent;
import marxo.entity.link.Link;
import marxo.entity.node.Event;
import marxo.entity.node.Node;
import marxo.entity.user.Tenant;
import marxo.entity.user.User;
import marxo.entity.user.UserType;
import marxo.entity.workflow.Workflow;
import marxo.test.BasicDataTests;
import marxo.tool.PasswordEncryptor;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Minutes;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.crypto.SecretKeyFactory;
import javax.xml.bind.DatatypeConverter;
import java.util.*;

@SuppressWarnings({"unchecked"})
public class DatabaseResetTests extends BasicDataTests {
	Workflow reusedWorkflow;

	@Test(priority = 10000)
	public void cleanDatabase() throws Exception {
		DB db = mongoTemplate.getDb();
		logger.info(String.format("Using database [%s]", db.getName()));

		Set<String> databaseNames = db.getCollectionNames();
		for (String databaseName : databaseNames) {
			if (!databaseName.startsWith("system")) {
				logger.info(String.format("Drop collection [%s]", databaseName));
				mongoTemplate.dropCollection(databaseName);
			}
		}
	}

	@Test(dependsOnMethods = {"cleanDatabase"})
	public void addTenant() throws Exception {
		Tenant tenant = new Tenant();
		tenant.setName("Marxo");
		tenant.description = "A tall, a good guy, and a cat.";
		tenant.phoneNumber = "(408) 888-8888";
		tenant.email = "marxo@gmail.com";

		FacebookData facebookData = new FacebookData();
		facebookData.accessToken = "CAADCM9YpGYwBANeLvBD7aswljKFqsBYZAAUZC9ohrKoPkR0OQ8yZA1kMZAIwBuLFsxPnnRaUsuIjB40Q9i8qn2BNlaITfkKsQYE4LFatfAY6okQgYe4b8fYcr400YdQP98Wp4SFZBG6MOMCtC3pJNsZCVB3bBpXZCyKvbj66SwBWjBW1ZAAZBYT2a";
		facebookData.expireTime = DateTime.parse("2014-01-14T08:22:54.541Z");
//		Assert.assertTrue(facebookData.updateToken());
		tenant.facebookData = facebookData;
		tenant.save();

		Assert.assertTrue(mongoTemplate.exists(Query.query(Criteria.where("name").is("Marxo")), Tenant.class));
	}

	@Test(dependsOnMethods = {"addTenant"})
	public void addUsers() throws Exception {
		ApplicationContext securityContext = new ClassPathXmlApplicationContext("classpath*:security.xml");
		byte[] salt = DatatypeConverter.parseHexBinary((String) securityContext.getBean("passwordSaltHexString"));
		SecretKeyFactory secretKeyFactory = (SecretKeyFactory) securityContext.getBean("secretKeyFactory");
		PasswordEncryptor passwordEncryptor = new PasswordEncryptor(salt, secretKeyFactory);

		Tenant tenant = mongoTemplate.findOne(Query.query(Criteria.where("name").is("Marxo")), Tenant.class);
		List<User> users = new ArrayList<>();
		{
			User user;

			user = new User();
			user.setTenant(tenant);
			user.setName("Tester");
			user.setEmail("test@example.com");
			user.setPassword(passwordEncryptor.encrypt("test"));
			user.gender = "GAY";
			users.add(user);

			user = new User();
			user.setTenant(tenant);
			user.setName("Wilson");
			user.setEmail("yyfearth@gmail.com");
			user.gender = "MALE";
			user.setPassword(passwordEncryptor.encrypt("2k96H29ECsJ05BJAkEGm6FC+UgjwVTc1qOd7SGG2uS8"));
			users.add(user);

			user = new User();
			user.setTenant(tenant);
			user.setName("Leo");
			user.setEmail("otaru14204@hotmail.com");
			user.gender = "MALE";
			user.setPassword(passwordEncryptor.encrypt("zhKhZMAp8rYymnIvM/rbcuNNrpG+Qgk+zVOS+x0n9mY"));
			users.add(user);

			mongoTemplate.indexOps(User.class).ensureIndex(new Index("email", Sort.Direction.ASC).unique().sparse());
		}

		for (User user : users) {
			user.type = UserType.PUBLISHER;
		}

		mongoTemplate.insert(users, User.class);

		Assert.assertEquals(mongoTemplate.count(new Query(), User.class), 3);
	}

	@Test(dependsOnMethods = {"addUsers"})
	public void addWorkflow() throws Exception {
		User user = User.getByEmail("yyfearth@gmail.com");
		Tenant tenant = user.getTenant();

		reusedWorkflow = new Workflow();
		reusedWorkflow.setTenant(tenant);
		reusedWorkflow.updateUserId = reusedWorkflow.updateUserId = user.id;
		reusedWorkflow.createTime = reusedWorkflow.updateTime = DateTime.now();
		reusedWorkflow.setName("Demo workflow");

		int nodeCount = 1;
		int contentCount = 1;

		Node node1 = new Node();
		node1.setName("Node " + nodeCount++);
		reusedWorkflow.addNode(node1);

		PostFacebookAction postFacebookAction1 = new PostFacebookAction();
		postFacebookAction1.setName("Post to Facebook 1");
		node1.addAction(postFacebookAction1);

		FacebookContent facebookContent1 = new FacebookContent();
		facebookContent1.setName("Contnet " + contentCount++);
		facebookContent1.message = String.format("Marxo Engine Automation [%s]\nThat's one small step for the engine, a giant leap for the project", facebookContent1.id);
		postFacebookAction1.setContent(facebookContent1);

		MonitorFacebookAction monitorFacebookAction1 = new MonitorFacebookAction();
		node1.addAction(monitorFacebookAction1);

		FacebookMonitorContent facebookMonitorContent1 = new FacebookMonitorContent();
		facebookMonitorContent1.setName("Contnet " + contentCount++);
		monitorFacebookAction1.setContent(facebookMonitorContent1);

		Node node2 = new Node();
		node2.setName("Node " + nodeCount++);
		reusedWorkflow.addNode(node2);

		PostFacebookAction postFacebookAction2 = new PostFacebookAction();
		postFacebookAction2.setName("Post to Facebook 2");
		node2.addAction(postFacebookAction2);

		Event event = new Event();
		event.setName("5 minutes later");
		event.setStartTime(DateTime.now().plus(Minutes.minutes(5)));
		event.setDuration(Days.days(1).toStandardDuration());
		postFacebookAction2.setEvent(event);

		FacebookContent facebookContent2 = new FacebookContent();
		facebookContent2.setName("Contnet " + contentCount++);
		facebookContent2.message = String.format("Follow up post [%s]", facebookContent2.id);
		postFacebookAction2.setContent(facebookContent2);

		MonitorFacebookAction monitorFacebookAction2 = new MonitorFacebookAction();
		node2.addAction(monitorFacebookAction2);

		FacebookMonitorContent facebookMonitorContent2 = new FacebookMonitorContent();
		facebookMonitorContent2.setName("Contnet " + contentCount++);
		monitorFacebookAction2.setContent(facebookMonitorContent2);

		Link link = new Link();
		link.setName("Just a link");
		link.setPreviousNode(node1);
		link.setNextNode(node2);
		reusedWorkflow.addLink(link);

		reusedWorkflow.wire();

		mongoTemplate.insertAll(Lists.newArrayList(
				reusedWorkflow,
				node1,
				facebookContent1,
				facebookMonitorContent1,
				node2,
				facebookContent2,
				event,
				facebookMonitorContent2,
				link
		));

		Assert.assertEquals(mongoTemplate.count(new Query(), Workflow.class), 1);
		Assert.assertEquals(mongoTemplate.count(new Query(), Node.class), 2);
		Assert.assertEquals(mongoTemplate.count(new Query(), Link.class), 1);
		Assert.assertEquals(mongoTemplate.count(new Query(), Content.class), 4);

		node1 = Node.get(node1.id);
		node2 = Node.get(node2.id);
		link = Link.get(link.id);
		Assert.assertEquals(node1.getToLinkIds().get(0), link.id);
	}

	@Test(dependsOnMethods = {"addWorkflow"})
	public void addProject() throws Exception {
		User user = User.getByEmail("yyfearth@gmail.com");
		Tenant tenant = user.getTenant();

		List<BasicEntity> entities = new ArrayList<>();
		Workflow workflow = new Cloner().deepClone(reusedWorkflow);
		workflow.id = new ObjectId();
		workflow.setName("Demo project");
		workflow.templateId = reusedWorkflow.id;
		workflow.isProject = true;
		workflow.updateUserId = workflow.createUserId = user.id;
		workflow.createTime = workflow.updateTime = DateTime.now();

		Map<ObjectId, ObjectId> linkMap = new HashMap<>();
		for (ObjectId objectId : workflow.linkIds) {
			linkMap.put(objectId, new ObjectId());
		}
		Map<ObjectId, ObjectId> nodeMap = new HashMap<>();
		for (ObjectId objectId : workflow.nodeIds) {
			nodeMap.put(objectId, new ObjectId());
		}

		workflow.startNodeId = nodeMap.get(workflow.startNodeId);

		for (Node node : workflow.getNodes()) {
			node.id = nodeMap.get(node.id);
			node.setWorkflow(workflow);

			for (Link link : node.getFromLinks()) {
				link.id = linkMap.get(link.id);
			}
			node.setFromLinks(node.getFromLinks());
			for (Link link : node.getToLinks()) {
				link.id = linkMap.get(link.id);
			}
			node.setToLinks(node.getToLinks());

			List<ObjectId> list = new ArrayList();
			for (ObjectId objectId : node.getFromNodeIds()) {
				list.add(nodeMap.get(objectId));
			}
			node.setFromNodeIds(list);
			list = new ArrayList();
			for (ObjectId objectId : node.getToNodeIds()) {
				list.add(nodeMap.get(objectId));
			}
			node.setToNodeIds(list);

			for (Action action : node.getActions()) {
				action.id = new ObjectId();
				action.setNode(node);

				Event event = action.getEvent();
				if (event != null) {
					event.id = new ObjectId();
					event.setNode(node);
					entities.add(event);
				}

				Content content = action.getContent();
				if (content != null) {
					content.id = new ObjectId();
					content.setNode(node);
					entities.add(content);
				}
			}
		}
		workflow.setNodes(workflow.getNodes());
		for (Link link : workflow.getLinks()) {
			link.id = linkMap.get(link.id);
			link.setWorkflow(workflow);
			link.previousNodeId = nodeMap.get(link.previousNodeId);
			link.nextNodeId = nodeMap.get(link.nextNodeId);
		}
		workflow.setLinks(workflow.getLinks());

		workflow.wire();

		entities.addAll(workflow.getNodes());
		entities.addAll(workflow.getLinks());
		entities.add(workflow);
		mongoTemplate.insertAll(entities);

		Criteria criteria = Criteria.where("workflowId").is(workflow.id);
		Query query = Query.query(criteria);
		Assert.assertEquals(mongoTemplate.count(query, Node.class), 2);
		Assert.assertEquals(mongoTemplate.count(query, Link.class), 1);
		Assert.assertEquals(mongoTemplate.count(query, Content.class), 4);
	}
}
