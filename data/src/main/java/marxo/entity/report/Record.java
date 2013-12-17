package marxo.entity.report;

import org.joda.time.DateTime;

public class Record {
	protected DateTime time = DateTime.now();

	public DateTime getTime() {
		return time;
	}

	public void setTime(DateTime time) {
		this.time = time;
	}
}
