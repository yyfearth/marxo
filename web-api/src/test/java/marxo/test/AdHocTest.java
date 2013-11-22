package marxo.test;

import marxo.entity.node.PostFacebook;
import marxo.entity.user.User;
import org.bson.types.ObjectId;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Test
public class AdHocTest {
	@Test
	public void testRegex() throws Exception {
		String text = "startNodeId";
		Pattern pattern = Pattern.compile("[A-Z]");
		Matcher matcher = pattern.matcher(text);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			String captured = matcher.group();
			matcher.appendReplacement(sb, "_" + captured.toLowerCase());
		}
		matcher.appendTail(sb);
		System.out.println(sb.toString());
	}

	@Test
	public void testCreateInstance() throws Exception {
		String fullName = PostFacebook.class.getName();
		Class<?> aClass = Class.forName(fullName);
		PostFacebook postFacebook = (PostFacebook) aClass.newInstance();
		Assert.assertNotNull(postFacebook);
	}

	@Test
	public void testCopyFields() throws Exception {
		User user = new User();
		user.tenantId = new ObjectId();
		user.setName("Tester");
		user.setEmail("test@example.com");
		user.setPassword("secret");
		Field[] fields = User.class.getFields();
		for (Field field : fields) {
			field.setAccessible(true);
			System.out.printf("'%s': '%s'\n", field.getName(), field.get(user));
		}
	}
}
