package marxo.entity.action;

import com.fasterxml.jackson.annotation.JsonProperty;
import marxo.entity.node.Event;
import org.springframework.data.mongodb.core.mapping.DBRef;

public abstract class TrackableAction extends Action {

	@JsonProperty("tracked")
	public boolean isTracked() {
		return trackEvent != null;
	}

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
