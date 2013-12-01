package marxo.test.remote;

import com.google.common.net.MediaType;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.types.User;
import marxo.entity.FacebookData;
import marxo.entity.FacebookStatus;
import marxo.entity.user.Tenant;
import marxo.test.ApiTestConfiguration;
import marxo.test.ApiTester;
import marxo.test.BasicApiTests;
import marxo.test.local.FacebookApiTests;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@ApiTestConfiguration(value = "http://masonwan.com/marxo/api/services/facebook")
public class RemoteFacebookApiTests extends FacebookApiTests {
}
