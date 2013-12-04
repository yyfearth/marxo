//package marxo.engine;
//
//import com.restfb.DefaultFacebookClient;
//import com.restfb.FacebookClient;
//import com.restfb.Parameter;
//import com.restfb.types.FacebookType;
//import com.restfb.types.Post;
//import marxo.entity.FacebookTask;
//import marxo.entity.MongoDbAware;
//import marxo.entity.action.PostFacebookAction;
//import marxo.entity.workflow.Notification;
//import marxo.entity.workflow.RunStatus;
//import marxo.tool.Loggable;
//import marxo.tool.StringTool;
//import org.joda.time.Seconds;
//import org.springframework.stereotype.Component;
//import org.springframework.util.Assert;
//
//@Component
//public class FacebookWorker implements Runnable, MongoDbAware, Loggable {
//
//	/*
//	Concurrent control
//	 */
//
//	public static Thread thread;
//	public static FacebookWorker facebookWorker;
//
//	public static void startAsync() {
//		facebookWorker = new FacebookWorker();
//		thread = new Thread(facebookWorker);
//		thread.start();
//	}
//
//	public static void stopAsync() {
//		facebookWorker.isStopped = true;
//		try {
//			thread.join(Seconds.seconds(10).toStandardDuration().getMillis());
//		} catch (InterruptedException e) {
//			logger.error(String.format("%s got error [%s] %s", facebookWorker, e.getClass().getSimpleName(), e.getMessage()));
//		} finally {
//			thread = null;
//			facebookWorker = null;
//		}
//	}
//
//	/*
//	Runner
//	 */
//
//	public volatile int count = 0;
//	public volatile boolean isStopped = false;
//
//	@Override
//	public void run() {
//		logger.debug(String.format("Facebook worker %s starts", this));
//
//		// todo: add dynamically changed wait time
//		try {
//			for (; !isStopped; Thread.sleep(Seconds.THREE.toStandardDuration().getMillis())) {
//				FacebookTask facebookTask = FacebookTask.next();
//				if (facebookTask == null) {
//					continue;
//				}
//
//				if (facebookTask.postFacebookAction == null ||
//						facebookTask.postFacebookAction.getTenant() == null ||
//						facebookTask.postFacebookAction.getTenant().facebookData == null ||
//						facebookTask.postFacebookAction.getTenant().facebookData.accessToken == null) {
//					logger.debug(String.format("%s encountered an invalid task %s", this, facebookTask));
//					continue;
//				}
//
//				logger.debug(String.format("%s is processing %s", facebookWorker, facebookTask));
//
//				FacebookClient facebookClient = new DefaultFacebookClient(facebookTask.postFacebookAction.getTenant().facebookData.accessToken);
//				PostFacebookAction postFacebookAction = facebookTask.postFacebookAction;
//
//				if (postFacebookAction.postId == null) {
//					FacebookContent content = postFacebookAction.getContent();
//					Assert.notNull(content, String.format("%s: content is null", this));
//					content.publishMessageResponse = facebookClient.publish("me/feed", FacebookType.class, Parameter.with("message", postFacebookAction.getContent().message));
//					content.save();
//
////					Notification notification = new Notification(Notification.Type.STARTED, Action.class);
//					Notification notification = new Notification();
//					notification.setName("Facebook message posted");
//					notification.setAction(postFacebookAction);
//					notification.save();
//
//					if (postFacebookAction.isTracked) {
//						postFacebookAction.status = RunStatus.MONITORING;
//					} else {
//						postFacebookAction.status = RunStatus.FINISHED;
//					}
//
//					postFacebookAction.save();
//				} else {
//					Post post = facebookClient.fetchObject(facebookTask.postFacebookAction.postId, Post.class);
//					FacebookMonitorContent.FacebookRecord facebookRecord = new FacebookMonitorContent.FacebookRecord();
//					// todo: insert the record to action.
//				}
//
//				count++;
//
//				// todo: update Facebook token
//			}
//		} catch (Exception e) {
//			logger.debug(String.format("%s ends with exception [%s] %s", this, e.getClass().getSimpleName(), e.getMessage()));
//			logger.debug(StringTool.exceptionToString(e));
//		}
//
//		logger.debug(String.format("Facebook worker %s stops", this));
//	}
//}
