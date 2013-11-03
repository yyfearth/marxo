package marxo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.types.User;
import marxo.entity.Workflow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testng.Assert.assertNotNull;

@WebAppConfiguration
@ContextHierarchy({
		@ContextConfiguration("classpath*:mongo-configuration.xml"),
		@ContextConfiguration("classpath*:security.xml"),
		@ContextConfiguration("classpath*:spring-security.xml"),
		@ContextConfiguration("classpath*:mvc-dispatcher-servlet.xml"),
})
public class WebApiTests extends AbstractTestNGSpringContextTests {
	@Autowired
	WebApplicationContext webApplicationContext;
	MockMvc mockMvc;

	@BeforeClass
	public void beforeClass() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@BeforeMethod
	public void setUp() throws Exception {
	}

	@AfterMethod
	public void tearDown() throws Exception {
	}

	@Test
	public void getWorkflows() throws Exception {
		MockHttpServletRequestBuilder builder = get("workflow").accept(MediaType.parseMediaType("application/json;charset=UTF-8"));
		mockMvc.perform(builder)
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON));
		ObjectMapper objectMapper = new ObjectMapper();
		ArrayList<Workflow> workflows = objectMapper.readValue(content().toString(), ArrayList.class);

		assertNotNull(workflows);
	}
}
