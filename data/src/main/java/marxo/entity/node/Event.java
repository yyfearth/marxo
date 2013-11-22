package marxo.entity.node;

import marxo.entity.BasicEntity;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.springframework.data.annotation.Transient;

public class Event extends BasicEntity {
	public ObjectId actionId;
	@Transient
	protected Action action;
	DateTime startTime;
	DateTime endTime;
	Duration duration;

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
		this.actionId = action.id;
	}

	// Write logic in each setter.

	public DateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(DateTime startTime) {
		this.startTime = startTime;
	}

	public DateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(DateTime endTime) {
		this.endTime = endTime;
	}

	public Duration getDuration() {
		return duration;
	}

	public void setDuration(Duration duration) {
		this.duration = duration;
	}
}
