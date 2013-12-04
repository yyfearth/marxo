package marxo.engine;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.types.FacebookType;
import com.restfb.types.Post;
import marxo.entity.FacebookTask;
import marxo.entity.MongoDbAware;
import marxo.entity.action.PostFacebookAction;
import marxo.entity.content.FacebookContent;
import marxo.entity.content.FacebookMonitorContent;
import marxo.entity.workflow.RunStatus;
import marxo.tool.Loggable;
import marxo.tool.StringTool;
import org.joda.time.Seconds;
import org.springframework.util.Assert;

public class FacebookWorker implements Runnable, MongoDbAware, Loggable {
	public volatile int count = 0;
	public volatile boolean isStopped = false;

	@Override
	public void run() {
		try {
			for (; !isStopped; Thread.sleep(Seconds.THREE.toStandardDuration().getMillis())) {
				FacebookTask facebookTask = FacebookTask.next();
				if (facebookTask == null) {
					continue;
				}

				if (facebookTask.postFacebookAction == null ||
						facebookTask.postFacebookAction.getTenant() == null ||
						facebookTask.postFacebookAction.getTenant().facebookData == null ||
						facebookTask.postFacebookAction.getTenant().facebookData.accessToken == null) {
					logger.debug(String.format("%s encountered an invalid task %s", this, facebookTask));
					continue;
				}

				FacebookClient facebookClient = new DefaultFacebookClient(facebookTask.postFacebookAction.getTenant().facebookData.accessToken);
				PostFacebookAction postFacebookAction = facebookTask.postFacebookAction;

				if (postFacebookAction.postId == null) {
					FacebookContent content = postFacebookAction.getContent();
					Assert.notNull(content, String.format("%s: content is null", this));
					content.publishMessageResponse = facebookClient.publish("me/feed", FacebookType.class, Parameter.with("message", postFacebookAction.getContent().message));
					content.save();

					if (postFacebookAction.isTracked) {
						postFacebookAction.status = RunStatus.MONITORING;
					} else {
						postFacebookAction.status = RunStatus.FINISHED;
					}

					postFacebookAction.save();
				} else {
					Post post = facebookClient.fetchObject(facebookTask.postFacebookAction.postId, Post.class);
					FacebookMonitorContent.FacebookRecord facebookRecord = new FacebookMonitorContent.FacebookRecord();
				}

				count++;

				// todo: update Facebook token
			}
		} catch (Exception e) {
			logger.debug(String.format("%s ends with exception [%s] %s", this, e.getClass().getSimpleName(), e.getMessage()));
			logger.debug(StringTool.exceptionToString(e));
		}
	}
}
