package marxo.entity.action;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.exception.FacebookException;
import com.restfb.types.Post;
import marxo.entity.Task;
import marxo.entity.content.FacebookMonitorContent;
import marxo.entity.workflow.Notification;
import marxo.entity.workflow.RunStatus;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.springframework.data.annotation.Transient;

public class MonitorFacebookAction extends Action {
	@Transient
	protected FacebookMonitorContent content;
	public String monitoredActionKey;
	public Period period = Period.days(1);
	public ObjectId facebookContentId;

	public DateTime nextTriggerTime;

	@Override
	public FacebookMonitorContent getContent() {
		return (content == null) ? (content = (FacebookMonitorContent) super.getContent()) : content;
	}

	@Override
	public boolean act() {
		if (getTenant().facebookData == null) {
			logger.debug(String.format("[%s] has no facebook info", this));
			return false;
		}

		if (getContent() == null) {
			logger.error(String.format("[%s] has no content", this));
			return false;
		}

		if (content.postId == null) {
			logger.error(String.format("[%s] has no post ID", this));
			return false;
		}

		if (nextTriggerTime != null) {
			if (nextTriggerTime.isBeforeNow()) {
				logger.debug(String.format("Reschedule action since time [%s] is not up yet.", nextTriggerTime));
				Task task = new Task(getNode().workflowId);
				task.time = nextTriggerTime;
				task.save();
				return true;
			}
		}

		try {
			FacebookClient facebookClient = new DefaultFacebookClient(tenant.facebookData.accessToken);
			Post post = facebookClient.fetchObject(content.postId, Post.class);
//			post.
//
//			Post post = facebookClient.fetchObject(content.postId, Post.class);
//			Record
//			post.getLikesCount();
//			content.publishMessageResponse = facebookClient.publish("me/feed", FacebookType.class, Parameter.with("message", content.message));
//			content.postId = content.publishMessageResponse.getId();
//			logger.debug(String.format("Submit Facebook post [%s]", content.publishMessageResponse));
		} catch (FacebookException e) {
			logger.debug(String.format("[%s] %s", e.getClass().getSimpleName(), e.getMessage()));
//			errors.add();

			// todo: put a notification under the tenant domain.
			Notification notification = new Notification();
//			notification

			return false;
		} finally {
			content.save();
		}

		status = RunStatus.FINISHED;

		return true;
	}
}
