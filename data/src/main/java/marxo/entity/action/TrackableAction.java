package marxo.entity.action;

import com.fasterxml.jackson.annotation.JsonProperty;
import marxo.entity.node.Event;

public abstract class TrackableAction extends Action {

	@JsonProperty("tracked")
	public boolean isTracked() {
		return trackEvent != null;
	}

	@JsonProperty("tracking")
	public Event trackEvent;

	@Override
	public void save() {
		if (trackEvent != null) {
			trackEvent.save();
		}
		super.save();
	}
}
