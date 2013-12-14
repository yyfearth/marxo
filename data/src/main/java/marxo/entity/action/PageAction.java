package marxo.entity.action;

import marxo.entity.Task;
import marxo.entity.node.Event;
import marxo.entity.workflow.Notification;
import marxo.entity.workflow.RunStatus;
import org.joda.time.DateTime;

public class PageAction extends TrackableAction {

	public PageAction() {
		type = Type.PAGE;
	}

	@Override
	public boolean act() {
		getEvent();

		if (event == null) {
			Event event1 = new Event();
			setEvent(event1);
		}

		if (event.getStartTime() == null) {
			event.setStartTime(DateTime.now());
			event.save();
		}

		if (event.getStartTime().isBeforeNow()) {
			status = RunStatus.STARTED;

			Notification notification = new Notification(Notification.Level.NORMAL, "Page is posted");
			notification.setAction(this);
			notification.save();

			getWorkflow().addTracableAction(this);
			workflow.save();

			return true;
		}

		if (event.getEndTime().isBeforeNow()) {
			status = RunStatus.FINISHED;

			Notification notification = new Notification(Notification.Level.NORMAL, "Page is finished");
			notification.setAction(this);
			notification.save();

			return true;
		}

		Task.reschedule(workflowId, event.getStartTime());

		return false;
	}
}
