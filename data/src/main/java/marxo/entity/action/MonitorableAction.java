package marxo.entity.action;

import marxo.entity.report.Record;

public abstract class MonitorableAction extends Action {

	public abstract Record monitor();
}
