package marxo.entity.node;

import marxo.entity.ActionChildEntity;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.springframework.data.annotation.Transient;

public class Event extends ActionChildEntity {
	protected DateTime startTime;
	protected DateTime endTime;
	protected Duration duration;

	public DateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(DateTime startTime) {
		this.startTime = startTime;
		if (endTime != null) {
			duration = new Duration(startTime.getMillis(), endTime.getMillis());
		} else if (duration != null) {
			endTime = startTime.plus(duration);
		}
	}

	public DateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(DateTime endTime) {
		this.endTime = endTime;
		if (startTime != null) {
			duration = new Duration(startTime.getMillis(), endTime.getMillis());
		}
	}

	public Duration getDuration() {
		return duration;
	}

	public void setDuration(Duration duration) {
		this.duration = duration;
		if (startTime != null) {
			endTime = startTime.plus(duration);
		}
	}

	public static Event get(ObjectId id) {
		return mongoTemplate.findById(id, Event.class);
	}
}
