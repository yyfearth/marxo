package marxo.test;

import com.google.common.collect.Lists;
import marxo.entity.content.Content;
import marxo.entity.link.Link;
import marxo.entity.node.Node;
import marxo.entity.user.User;
import marxo.entity.workflow.Workflow;
import marxo.tool.Loggable;
import marxo.validation.SelectIdFunction;
import org.bson.types.ObjectId;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class BasicApiTests implements Loggable {
	protected final ApplicationContext dataContext = new ClassPathXmlApplicationContext("mongo-configuration.xml");
	protected final MongoTemplate mongoTemplate = dataContext.getBean(MongoTemplate.class);
	protected String baseUrl;
	protected String email;
	protected String password;
	protected User user;
	protected List<Workflow> workflowsToBeRemoved = new ArrayList<>();
	protected List<Node> nodesToBeRemoved = new ArrayList<>();
	protected List<Link> linksToBeRemoved = new ArrayList<>();
	protected List<Content> contentsToBeRemoved = new ArrayList<>();

	protected BasicApiTests() {
		ApiTestConfiguration configuration = getClass().getAnnotation(ApiTestConfiguration.class);
		if (configuration == null) {
			return;
		}
		baseUrl = configuration.value();
		email = configuration.email();
		password = configuration.password();
	}

	@BeforeClass
	public void beforeClass() {
	}

	@AfterClass
	public void afterClass() throws IOException {
		List<ObjectId> objectIds;

		objectIds = Lists.transform(workflowsToBeRemoved, SelectIdFunction.getInstance());
		mongoTemplate.remove(Query.query(Criteria.where("id").in(objectIds)), Workflow.class);

		objectIds = Lists.transform(nodesToBeRemoved, SelectIdFunction.getInstance());
		mongoTemplate.remove(Query.query(Criteria.where("id").in(objectIds)), Node.class);

		objectIds = Lists.transform(linksToBeRemoved, SelectIdFunction.getInstance());
		mongoTemplate.remove(Query.query(Criteria.where("id").in(objectIds)), Link.class);

		objectIds = Lists.transform(contentsToBeRemoved, SelectIdFunction.getInstance());
		mongoTemplate.remove(Query.query(Criteria.where("id").in(objectIds)), Content.class);
	}

	@BeforeMethod
	public void beforeMethod() throws Exception {
	}

	@AfterMethod
	public void afterMethod() throws Exception {
	}
}
