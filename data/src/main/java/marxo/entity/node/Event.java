package marxo.entity.node;

import marxo.entity.BasicEntity;
import org.joda.time.DateTime;
import org.joda.time.Duration;

public class Event extends BasicEntity {
	DateTime startTime;
	DateTime endTime;
	Duration duration;

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
