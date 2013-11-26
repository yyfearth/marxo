package marxo.test;

import com.google.common.collect.Collections2;
import marxo.entity.BasicEntity;
import marxo.entity.user.User;
import marxo.tool.Loggable;
import marxo.validation.SelectIdFunction;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.testng.SkipException;
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

	protected List<BasicEntity> entitiesToSave = new ArrayList<>();
	protected List<BasicEntity> entitiesToRemove = new ArrayList<>();

	/**
	 * Save everything in `entitiesToSave`, and add them in `entitiesToRemove`. Also clear `entitiesToSave`.
	 */
	public void insertAll() {
		mongoTemplate.insertAll(entitiesToSave);
		entitiesToRemove.addAll(entitiesToSave);
		entitiesToSave.clear();
	}

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
		Criteria criteria = Criteria.where("email").is(email);
		user = mongoTemplate.findOne(Query.query(criteria), User.class);
		if (user == null) {
			throw new SkipException(String.format("Cannot find email [%s]", email));
		}
	}

	@AfterClass
	public void afterClass() throws IOException {
		Criteria criteria = Criteria.where("_id").in(Collections2.transform(entitiesToRemove, SelectIdFunction.getInstance()));
		for (String collectionName : mongoTemplate.getCollectionNames()) {
			mongoTemplate.remove(Query.query(criteria), collectionName);
		}
	}

	@BeforeMethod
	public void beforeMethod() throws Exception {
	}

	@AfterMethod
	public void afterMethod() throws Exception {
	}
}
