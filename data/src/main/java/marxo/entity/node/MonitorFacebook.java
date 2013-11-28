package marxo.entity.node;

import marxo.entity.Task;
import marxo.entity.content.FacebookMonitorContent;
import marxo.entity.workflow.RunStatus;
import marxo.exception.KeyNotFoundException;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.springframework.data.annotation.Transient;

public class MonitorFacebook extends Action {
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
		if (!super.act()) {
			return false;
		}

		if (getTenant().facebookData == null) {
			logger.debug(String.format("Tenant [%s] has no facebook info", getTenant().id));
			return false;
		}

		if (getEvent() != null && event.startTime.isBeforeNow()) {
			Task task = new Task(getNode().workflowId);
			task.time = event.startTime;
			task.save();

			status = RunStatus.WAITING;
			save();
			return true;
		}

		if (getEvent().getStartTime() == null || event.getStartTime().isAfterNow()) {
			// do it.
		} else {
			// reschedule.
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

//		try {
//			FacebookClient facebookClient = new DefaultFacebookClient(tenant.facebookData.accessToken);
//
//			if (getContent().postId == null) {
//				FacebookContent facebookContent = (FacebookContent) Content.get(facebookContentId);
//				content.postId = facebookContent.publishMessageResponse.getId();
//			}
//
//			Post post = facebookClient.fetchObject(content.postId, Post.class);
//			Record
//			post.getLikesCount();
//			content.publishMessageResponse = facebookClient.publish("me/feed", FacebookType.class, Parameter.with("message", content.message));
//			content.postId = content.publishMessageResponse.getId();
//			logger.debug(String.format("Submit Facebook post [%s]", content.publishMessageResponse));
//		} catch (FacebookException e) {
//			logger.debug(String.format("[%s] %s", e.getClass().getSimpleName(), e.getMessage()));
//			content.errorMessage = e.getMessage();
//			// todo: put a notification under the tenant domain.
//			return false;
//		} finally {
//			content.save();
//		}

		node.save();

		return true;
	}

	@Override
	public void wire() {
		super.wire();

		if (monitoredActionKey == null) {
			throw new KeyNotFoundException(monitoredActionKey, this);
		}

		String[] keys = monitoredActionKey.split("\\.");
	}
}
