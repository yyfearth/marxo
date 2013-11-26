package marxo.test;

import marxo.entity.user.User;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;

public abstract class BasicApiTests extends BasicDataTests {
	protected String baseUrl;
	protected String email;
	protected String password;
	protected User user;

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
	@Override
	public void beforeClass() throws Exception {
		super.beforeClass();

		Criteria criteria = Criteria.where("email").is(email);
		user = mongoTemplate.findOne(Query.query(criteria), User.class);
		if (user == null) {
			throw new SkipException(String.format("Cannot find email [%s]", email));
		}
	}

}
