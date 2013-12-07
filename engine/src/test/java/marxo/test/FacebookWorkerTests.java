//package marxo.test;
//
//import com.restfb.DefaultFacebookClient;
//import com.restfb.FacebookClient;
//import marxo.engine.FacebookWorker;
//import marxo.entity.FacebookData;
//import marxo.entity.FacebookTask;
//import marxo.entity.MongoDbAware;
//import marxo.entity.action.Action;
//import marxo.entity.action.PostFacebookAction;
//import marxo.entity.node.Node;
//import marxo.entity.user.Tenant;
//import marxo.entity.workflow.RunStatus;
//import org.joda.time.DateTime;
//import org.testng.Assert;
//import org.testng.annotations.AfterClass;
//import org.testng.annotations.BeforeClass;
//import org.testng.annotations.Test;
//
//public class FacebookWorkerTests extends BasicDataTests implements MongoDbAware {
//	FacebookClient facebookClient;
//
//	Tenant reusedTenant;
//
//	@BeforeClass
//	@Override
//	public void beforeClass() throws Exception {
//		super.beforeClass();
//
//		FacebookWorker.startAsync();
//
//		reusedTenant = new Tenant();
//		reusedTenant.setName("Marxo");
//		reusedTenant.description = "A tall, a good guy, and a cat.";
//		reusedTenant.phoneNumber = "(408) 888-8888";
//		reusedTenant.email = "marxo@gmail.com";
//
//		FacebookData facebookData = new FacebookData();
//		facebookData.accessToken = "CAADCM9YpGYwBANeLvBD7aswljKFqsBYZAAUZC9ohrKoPkR0OQ8yZA1kMZAIwBuLFsxPnnRaUsuIjB40Q9i8qn2BNlaITfkKsQYE4LFatfAY6okQgYe4b8fYcr400YdQP98Wp4SFZBG6MOMCtC3pJNsZCVB3bBpXZCyKvbj66SwBWjBW1ZAAZBYT2a";
//		facebookData.expireTime = DateTime.parse("2014-01-14T08:22:54.541Z");
//		reusedTenant.facebookData = facebookData;
//
//		insertEntities(
//				reusedTenant
//		);
//
//		facebookClient = new DefaultFacebookClient(reusedTenant.facebookData.accessToken);
//	}
//
//	@AfterClass
//	@Override
//	public void afterClass() throws Exception {
//
//		FacebookWorker.stop();
//
//		super.afterClass();
//	}
//
//	@Test
//	public void post() throws Exception {
//
//		Node node = new Node();
//		node.setName("Test Node for Engine");
//		node.setTenant(reusedTenant);
//
//		PostFacebookAction postFacebookAction = new PostFacebookAction();
//		postFacebookAction.setName("Test Action for Engine");
//		postFacebookAction.isTracked = false;
//		node.addAction(postFacebookAction);
//
//		FacebookContent facebookContent = new FacebookContent();
//		facebookContent.message = String.format("Marxo Engine Automation [%s]\nThat's one small step for the engine, a giant leap for the project", facebookContent.id);
//		facebookContent.actionId = postFacebookAction.id;
//		postFacebookAction.setContent(facebookContent);
//
//		FacebookTask facebookTask = new FacebookTask(postFacebookAction);
//
//		int count = FacebookWorker.facebookWorker.count;
//		insertEntities(
//				node,
//				facebookContent,
//				facebookTask
//		);
//
//		DateTime due = DateTime.now().plusSeconds(10);
//		while (due.isAfterNow()) {
//			if (FacebookWorker.facebookWorker.count != count) {
//				break;
//			}
//
//			Thread.sleep(100);
//		}
//
//		postFacebookAction = (PostFacebookAction) Action.get(postFacebookAction.id);
//		Assert.assertEquals(postFacebookAction.status, RunStatus.FINISHED);
//	}
//
//	@Test
//	public void testName() throws Exception {
//
//		Node node = new Node();
//		node.setName("Test Node for Engine");
//		node.setTenant(reusedTenant);
//
//		PostFacebookAction postFacebookAction = new PostFacebookAction();
//		postFacebookAction.setName("Test Action for Engine");
//		postFacebookAction.isTracked = false;
//		node.addAction(postFacebookAction);
//
//		FacebookContent facebookContent = new FacebookContent();
//		facebookContent.message = String.format("Marxo Engine Automation [%s]\nThat's one small step for the engine, a giant leap for the project", facebookContent.id);
//		facebookContent.actionId = postFacebookAction.id;
//		postFacebookAction.setContent(facebookContent);
//
//		FacebookTask facebookTask = new FacebookTask(postFacebookAction);
//
//		int count = FacebookWorker.facebookWorker.count;
//		insertEntities(
//				node,
//				facebookContent,
//				facebookTask
//		);
//	}
//}
