package marxo;

import marxo.entity.node.PostFacebook;
import org.testng.Assert;
import org.testng.annotations.Test;

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
		String simpleName = PostFacebook.class.getSimpleName();
		String fullName = PostFacebook.class.getName();
		Class<?> aClass = Class.forName(fullName);
		PostFacebook postFacebook = (PostFacebook) aClass.newInstance();
		Assert.assertNotNull(postFacebook);

	}
}
