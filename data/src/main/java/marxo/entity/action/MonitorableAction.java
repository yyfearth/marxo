package marxo.entity.action;

import marxo.entity.report.Record;
import org.joda.time.Duration;
import org.joda.time.Period;

public abstract class MonitorableAction extends Action {

	public abstract Record monitor();
}
