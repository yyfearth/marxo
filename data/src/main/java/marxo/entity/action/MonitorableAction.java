package marxo.entity.action;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.Duration;
import org.joda.time.Period;

public abstract class MonitorableAction extends Action {

	@JsonProperty("tracked")
	public Boolean isMonitored = true;

	public Duration monitorDuration;
	public Period monitorPeriod;
}
