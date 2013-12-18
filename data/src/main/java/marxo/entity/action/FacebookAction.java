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
import marxo.exception.DataInconsistentException;
import org.joda.time.DateTime;

public class FacebookAction extends TrackableAction {

	public FacebookAction() {
		type = Type.FACEBOOK;
	}

	@Override
	public boolean act(Workflow workflow, Node node) {

		if (!workflow.id.equals(workflowId)) {
			throw new DataInconsistentException(String.format("Workflow IDs [%s] and [%s] are not same", workflow.id, workflowId));
		}

		// Make sure the existence of access token.
		if (getTenant().facebookData == null || getTenant().facebookData.accessToken == null) {
			logger.info(String.format("%s doesn't have Facebook to continue", this));

			Notification.saveNew(Notification.Level.CRITICAL, this, Notification.Type.FACEBOOK_TOKEN);
			setStatus(RunStatus.ERROR);

			return false;
		}

		FacebookClient facebookClient = new DefaultFacebookClient(getTenant().facebookData.accessToken);

		try {
			if (getStatus().equals(RunStatus.IDLE)) {
				String postedMessage = String.format("%s\n\n(Posted by Marxo at %s)", getContent().message, DateTime.now().toLocalTime().toString("k:m"));

				getContent().messageResponse = facebookClient.publish("me/feed", FacebookType.class, Parameter.with("message", postedMessage));
				getContent().setPostedTime(DateTime.now());
				logger.debug(String.format("Submit Facebook post [%s]", content.messageResponse));

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

					Notification.saveNew(Notification.Level.NORMAL, this, Notification.Type.TRACKED);

					nextTrackTime = trackEvent.getStartTime().plus(trackPeriod);
					Task.schedule(workflowId, nextTrackTime);
					return true;
				}
			}

			if (getStatus().equals(RunStatus.TRACKED)) {
				if (!trackEvent.getEndTime().isAfterNow()) {
					setStatus(RunStatus.FINISHED);
					return true;
				}

				if (nextTrackTime.isAfterNow()) {
					Task.schedule(workflowId, nextTrackTime);
					return true;
				}

				Post post = facebookClient.fetchObject(getContent().getPostId(), Post.class);
				getContent().records.add(FacebookRecord.getInstance(post));

				logger.debug(String.format("Fetch Facebook post [%s]", post.getId()));

				nextTrackTime = nextTrackTime.plus(trackPeriod);
				Task.schedule(workflowId, nextTrackTime);

				return true;
			}
		} catch (FacebookException e) {
			logger.warn(String.format("%s [%s] %s", this, e.getClass().getSimpleName(), e.getMessage()));

			Notification.saveNew(Notification.Level.CRITICAL, this, Notification.Type.FACEBOOK_TOKEN);
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
