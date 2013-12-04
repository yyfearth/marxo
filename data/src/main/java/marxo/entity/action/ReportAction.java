//package marxo.entity.action;
//
//import marxo.entity.report.Report;
//import org.bson.types.ObjectId;
//import org.springframework.data.annotation.Transient;
//
//public abstract class ReportAction extends Action {
//
//	/*
//	Report
//	 */
//
//	public ObjectId reportId;
//
//	@Transient
//	public Report report;
//
//	public Report getReport() {
//		if (reportId == null) {
//			return report = null;
//		}
//		return (report == null) ? (report = Report.get(reportId)) : report;
//	}
//
//	public void setReport(Report report) {
//		this.report = report;
//		if (report != null) {
//			this.reportId = report.id;
//		}
//	}
//}
