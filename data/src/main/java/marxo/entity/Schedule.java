package marxo.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import marxo.entity.action.Context;
import org.joda.time.DateTime;
import org.joda.time.Duration;

public class Schedule extends Context {
	@JsonProperty()
	public DateTime startDate;
	public DateTime endDate;
	// todo: make sure the contextType is acceptable by ObjectMapper.
	public Duration duration;
}
