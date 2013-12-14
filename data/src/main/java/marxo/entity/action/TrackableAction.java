package marxo.entity.action;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.Duration;
import org.joda.time.Period;

public abstract class TrackableAction extends Action {

	@JsonProperty("tracked")
	public Boolean isTracked = true;

	public Duration trackDuration;
	public Period trackPeriod;
}
