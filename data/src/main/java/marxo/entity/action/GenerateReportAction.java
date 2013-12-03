package marxo.entity.action;

import com.google.common.collect.Lists;
import marxo.entity.report.Record;
import marxo.entity.report.Report;
import marxo.entity.workflow.Notification;
import marxo.entity.workflow.RunStatus;
import marxo.tool.StringTool;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.List;

public class GenerateReportAction extends ReportAction {
	public List<ObjectId> monitoredActionIds = new ArrayList<>();

	@Transient
	protected List<MonitorableAction> monitoredActions;

	public List<MonitorableAction> getMonitoredActions() {
		if (monitoredActionIds.isEmpty()) {
			return monitoredActions = new ArrayList<>();
		}
		return (monitoredActions == null) ? (monitoredActions = mongoTemplate.find(Query.query(Criteria.where("_id").in(monitoredActionIds)), MonitorableAction.class)) : monitoredActions;
	}

	public void addMonitoredAction(MonitorableAction monitorableAction) {
		if (monitoredActions == null) {
			monitoredActions = Lists.newArrayList(monitorableAction);
		} else {
			monitoredActions.add(monitorableAction);
		}
		monitoredActionIds.add(monitorableAction.id);
	}

	@Override
	public boolean act() {
		if (reportId == null) {
			setReport(new Report());
			report.save();
		}

		if (getTenant().facebookData == null) {
			logger.debug(String.format("%s has no Facebook token", getTenant()));
			Notification notification = new Notification();
			notification.type = Notification.Type.DEFAULT;
			notification.save();
			return false;
		}

		try {
			for (MonitorableAction monitorableAction : getMonitoredActions()) {
				if (monitorableAction.isTracked) {
					Record record = monitorableAction.monitor();
					Report.addRecord(report, monitorableAction, record);
				}
			}
		} catch (Exception e) {
			logger.debug(String.format("%s got error [%s] %s", this, e.getClass().getSimpleName(), e.getMessage()));
			logger.debug(StringTool.exceptionToString(e));
			return false;
		}

		status = RunStatus.FINISHED;
		return true;
	}
}
