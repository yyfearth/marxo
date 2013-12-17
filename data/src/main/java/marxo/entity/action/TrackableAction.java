package marxo.entity.action;

import com.fasterxml.jackson.annotation.JsonProperty;
import marxo.entity.node.Event;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Seconds;
import org.springframework.data.mongodb.core.mapping.DBRef;

public abstract class TrackableAction extends Action {

	static Duration trackPeriod = Seconds.seconds(10).toStandardDuration();

	@JsonProperty("tracking")
	@DBRef
	protected Event trackEvent;

	public Event getTrackEvent() {
		return trackEvent;
	}

	public void setTrackEvent(Event trackEvent) {
		this.trackEvent = trackEvent;
		if (trackEvent != null) {
			trackEvent.setAction(this);
		}
	}

	@JsonProperty("tracked")
	public boolean isTracked() {
		return trackEvent != null;
	}

	/*
	nextTrackTime
	 */

	protected DateTime nextTrackTime;

	public DateTime getNextTrackTime() {
		return nextTrackTime;
	}

	public void setNextTrackTime(DateTime nextTrackTime) {
		this.nextTrackTime = nextTrackTime;
	}

	/*
	DAO
	 */

	@Override
	public void save() {
		if (trackEvent != null) {
			trackEvent.save();
		}
		super.save();
	}

	@Override
	public void remove() {
		super.remove();
		if (trackEvent != null) {
			trackEvent.remove();
		}
	}
}
