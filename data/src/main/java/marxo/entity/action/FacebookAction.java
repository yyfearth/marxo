package marxo.entity.action;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.exception.FacebookException;
import com.restfb.types.FacebookType;
import com.restfb.types.Post;
import marxo.entity.Task;
import marxo.entity.node.Node;
import marxo.entity.report.FacebookRecord;
import marxo.entity.workflow.Notification;
import marxo.entity.workflow.RunStatus;
import marxo.entity.workflow.Workflow;
import org.joda.time.DateTime;

public class FacebookAction extends TrackableAction {

	public FacebookAction() {
		type = Type.FACEBOOK;
	}

	@Override
	public boolean act(Workflow workflow, Node node) {

		// Make sure the existence of access token.
		if (getTenant().facebookData == null || getTenant().facebookData.accessToken == null) {
			logger.info(String.format("%s doesn't have Facebook to continue", this));

			Notification.saveNew(Notification.Level.CRITICAL, tenant, "Please update the Facebook permission");
			setStatus(RunStatus.ERROR);

			return false;
		}

		FacebookClient facebookClient = new DefaultFacebookClient(getTenant().facebookData.accessToken);

		try {
			if (getStatus().equals(RunStatus.IDLE)) {
				getContent().messageResponse = facebookClient.publish("me/feed", FacebookType.class, Parameter.with("message", getContent().message));
				content.save();
				logger.info(String.format("Submit Facebook post [%s]", content.messageResponse));

				setStatus(RunStatus.STARTED);
			}

			if (getStatus().equals(RunStatus.STARTED)) {
				if (!isTracked()) {
					setStatus(RunStatus.FINISHED);
					return true;
				}

				if (trackEvent.getStartTime() == null) {
					trackEvent.setStartTime(DateTime.now());
				}

				if (!trackEvent.getEndTime().isAfterNow()) {
					setStatus(RunStatus.FINISHED);
					return true;
				}

				if (!trackEvent.getStartTime().isAfterNow()) {
					setStatus(RunStatus.TRACKED);
					workflow.addTracableAction(this);

					Notification.saveNew(Notification.Level.NORMAL, this, "Facebook post is tracked");

					nextTrackTime = trackEvent.getStartTime();
					Task.reschedule(workflowId, nextTrackTime);
					return true;
				}
			}

			if (getStatus().equals(RunStatus.TRACKED)) {
				if (!trackEvent.getEndTime().isAfterNow()) {
					setStatus(RunStatus.FINISHED);
					return true;
				}

				if (nextTrackTime.isAfterNow()) {
					Task.reschedule(workflowId, nextTrackTime);
					return true;
				}

				Post post = facebookClient.fetchObject(getContent().getPostId(), Post.class);
				getContent().records.add(FacebookRecord.getInstance(post));

				logger.debug(String.format("Fetch Facebook post [%s]", post.getId()));

				nextTrackTime = nextTrackTime.plus(trackPeriod);
				Task.reschedule(workflowId, nextTrackTime);

				return true;
			}
		} catch (FacebookException e) {
			logger.info(String.format("%s [%s] %s", this, e.getClass().getSimpleName(), e.getMessage()));

			Notification.saveNew(Notification.Level.CRITICAL, tenant, "Please update the Facebook permission");
			setStatus(RunStatus.ERROR);

			return false;
		}

		if (getStatus().equals(RunStatus.FINISHED)) {
			return true;
		}

		logger.warn(String.format("%s has unexpected status [%s]", this, getStatus()));

		return false;
	}
}
