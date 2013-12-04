package marxo.test;

import com.google.common.collect.Lists;
import marxo.entity.workflow.Notification;
import org.testng.Assert;
import org.testng.annotations.Test;

public class DataTests extends BasicDataTests {

	@Test
	public void whetherInsertDoUpdate() throws Exception {
		Notification notification = new Notification();

		insertEntities(notification);

		notification.setName("Hello world");
		mongoTemplate.insert(notification);

		notification = mongoTemplate.findById(notification.id, Notification.class);
		Assert.assertNotEquals(notification.getName(), "Hello world");

		notification.setName("Hello world");
		mongoTemplate.insertAll(Lists.newArrayList(notification));

		notification = mongoTemplate.findById(notification.id, Notification.class);
		Assert.assertNotEquals(notification.getName(), "Hello world");

		notification.setName("Hello world");
		mongoTemplate.save(notification);

		notification = mongoTemplate.findById(notification.id, Notification.class);
		Assert.assertEquals(notification.getName(), "Hello world");
	}
}
