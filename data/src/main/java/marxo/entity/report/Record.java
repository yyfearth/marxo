package marxo.entity.report;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.DateTime;

public class Record {
	@JsonProperty("createdAt")
	protected DateTime time = DateTime.now();

	public DateTime getTime() {
		return time;
	}

	public void setTime(DateTime time) {
		this.time = time;
	}
}
