package marxo.entity.action;

import marxo.entity.Task;
import marxo.entity.node.Node;
import marxo.entity.report.PageRecord;
import marxo.entity.workflow.Notification;
import marxo.entity.workflow.RunStatus;
import marxo.entity.workflow.Workflow;
import org.joda.time.DateTime;

public class PageAction extends TrackableAction {

	public PageAction() {
		type = Type.PAGE;
	}

	@Override
	public boolean act(Workflow workflow, Node node) {
		if (getStatus().equals(RunStatus.IDLE)) {
			setStatus(RunStatus.STARTED);
		}

		if (getStatus().equals(RunStatus.STARTED)) {
			if (!isTracked()) {
				logger.warn(String.format("%s should be tracking instead of finishing directly", this));
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

				Notification.saveNew(Notification.Level.NORMAL, this, "Page is tracked");

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

			Content content1 = getContent();
			content1.records.add(PageRecord.getInstance(content1));

			nextTrackTime = nextTrackTime.plus(trackPeriod);
			Task.reschedule(workflowId, nextTrackTime);

			return true;
		}

		if (getStatus().equals(RunStatus.FINISHED)) {
			return true;
		}

		logger.warn(String.format("%s has unexpected status [%s]", this, getStatus()));

		return false;
	}
}
