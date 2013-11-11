package marxo.entity;

import org.joda.time.DateTime;
import org.joda.time.Duration;

public class Event extends Context {
	public DateTime startDate;
	public DateTime endDate;
	// todo: make sure the type is acceptable by ObjectMapper.
	public Duration duration;
}
