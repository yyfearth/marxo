package marxo.entity.action;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.exception.FacebookException;
import com.restfb.types.FacebookType;
import com.restfb.types.Post;
import marxo.entity.Task;
import marxo.entity.workflow.Notification;
import marxo.entity.workflow.RunStatus;
import org.joda.time.DateTime;

public class FacebookAction extends TrackableAction {

	public FacebookAction() {
		type = Type.FACEBOOK;
	}

	@Override
	public boolean act() {

		// Make sure the existence of access token.
		if (getTenant().facebookData == null || getTenant().facebookData.accessToken == null) {
			logger.info(String.format("%s doesn't have Facebook to continue", this));

			Notification notification = new Notification(Notification.Level.CRITICAL, "Please update your Facebook permission");
			notification.setTenant(getTenant());
			notification.save();
			status = RunStatus.ERROR;

			return false;
		}

		FacebookClient facebookClient = new DefaultFacebookClient(getTenant().facebookData.accessToken);

		try {
			if (getContent().getPostId() == null) {
				getContent().messageResponse = facebookClient.publish("me/feed", FacebookType.class, Parameter.with("message", getContent().message));
				content.save();
				logger.info(String.format("Submit Facebook post [%s]", content.messageResponse));

				getEvent();
				if (isTracked && event != null) {
					status = RunStatus.TRACKED;

					getWorkflow().addTracableAction(this);
					getWorkflow().save();

					Notification notification = new Notification(Notification.Level.NORMAL, "Start tracking");
					notification.setAction(this);
					notification.save();

					getEvent();
					if (event.getStartTime() == null) {
						event.setStartTime(DateTime.now());
						event.save();
					}
					Task.reschedule(workflowId, event.getStartTime());
				} else {
					status = RunStatus.FINISHED;
				}
			} else {
				if (!isTracked || getEvent() == null) {
					status = RunStatus.FINISHED;
					return true;
				}

				Post post = facebookClient.fetchObject(getContent().getPostId(), Post.class);
				Content.FacebookRecord facebookRecord = Content.FacebookRecord.fromPost(post);
				getContent().records.add(facebookRecord);
			}
		} catch (FacebookException e) {
			logger.info(String.format("%s [%s] %s", this, e.getClass().getSimpleName(), e.getMessage()));

			Notification notification = new Notification(Notification.Level.CRITICAL, "Please update the Facebook permission");
			notification.setTenant(getTenant());
			notification.save();
			status = RunStatus.ERROR;

			return false;
		}

		return true;
	}
}
