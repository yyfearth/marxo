package marxo.test;

import marxo.entity.user.Tenant;
import marxo.entity.user.User;
import marxo.tool.PasswordEncryptor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;

import javax.crypto.SecretKeyFactory;
import javax.xml.bind.DatatypeConverter;
import java.net.URISyntaxException;

public abstract class BasicApiTests extends BasicDataTests {
	protected String baseUrl;
	protected String email;
	protected String password;
	protected String facebookId;
	protected String facebookToken;

	protected Tenant reusedTenant;
	protected User reusedUser;
	protected ApiTesterBuilder apiTesterBuilder;

	ApplicationContext securityContext = new ClassPathXmlApplicationContext("classpath*:security.xml");
	byte[] salt = DatatypeConverter.parseHexBinary((String) securityContext.getBean("passwordSaltHexString"));
	SecretKeyFactory secretKeyFactory = (SecretKeyFactory) securityContext.getBean("secretKeyFactory");
	PasswordEncryptor passwordEncryptor = new PasswordEncryptor(salt, secretKeyFactory);

	protected BasicApiTests() {
		ApiTestConfiguration configuration = getClass().getAnnotation(ApiTestConfiguration.class);
		if (configuration == null) {
			return;
		}
		baseUrl = configuration.value();
		email = getClass().getSimpleName().toLowerCase() + "@meow.com";
		password = "test";
		facebookId = configuration.facebookId();
		facebookToken = configuration.facebookToken();

		try {
			apiTesterBuilder = new ApiTesterBuilder().baseUrl(baseUrl).basicAuth(email, password);
		} catch (URISyntaxException e) {
			throw new SkipException(String.format("Cannot start test case due to: [%s] %s", e.getClass().getSimpleName(), e.getMessage()), e);
		}
	}

	@BeforeClass
	@Override
	public void beforeClass() throws Exception {
		super.beforeClass();

		reusedTenant = new Tenant();
		reusedTenant.setName("Marxo");
		reusedTenant.description = "A tall, a good guy, and a cat.";
		reusedTenant.phoneNumber = "(408) 888-8888";
		reusedTenant.email = "marxo@gmail.com";

		reusedUser = new User();
		reusedUser.setTenant(reusedTenant);
		reusedUser.setName("Test user");
		reusedUser.setEmail(email);
		reusedUser.setPassword(passwordEncryptor.encrypt(password));

		insertEntities(
				reusedTenant,
				reusedUser
		);
	}
}
