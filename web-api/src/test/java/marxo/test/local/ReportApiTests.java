//package marxo.test.local;
//
//import com.google.common.net.MediaType;
//import marxo.entity.action.GenerateReportAction;
//import marxo.entity.node.Node;
//import marxo.entity.report.Report;
//import marxo.entity.workflow.Workflow;
//import marxo.test.ApiTestConfiguration;
//import marxo.test.ApiTester;
//import marxo.test.BasicApiTests;
//import org.testng.Assert;
//import org.testng.annotations.BeforeClass;
//import org.testng.annotations.Test;
//
//@ApiTestConfiguration("http://localhost:8080/api/report/")
//public class ReportApiTests extends BasicApiTests {
//	Report reusedReport;
//
//	Workflow reusedWorkflow;
//	Node reusedNode;
//	GenerateReportAction ueusedAction;
//
//	@BeforeClass
//	@Override
//	public void beforeClass() throws Exception {
//		super.beforeClass();
//
//		reusedWorkflow = new Workflow();
//
//		reusedNode = new Node();
//		reusedWorkflow.addNode(reusedNode);
//
//		ueusedAction = new GenerateReportAction();
//		reusedNode.addAction(ueusedAction);
//
//		insertEntities(
//				reusedWorkflow,
//				reusedNode,
//				ueusedAction
//		);
//	}
//
//	@Test
//	public void createReport() throws Exception {
//		reusedReport = new Report();
//		reusedReport.setName("Testing report");
//		ueusedAction.setReport(reusedReport);
//
//		try (ApiTester apiTester = apiTesterBuilder.build()) {
//			apiTester
//					.httpPost(reusedReport)
//					.send();
//			apiTester
//					.isCreated()
//					.matchContentType(MediaType.JSON_UTF_8);
//			Report report1 = apiTester.getContent(Report.class);
//			Assert.assertNotNull(report1);
//			Assert.assertEquals(report1.id, reusedReport.id);
//		}
//
//		Report report1 = Report.get(reusedReport.id);
//		Assert.assertEquals(report1.id, reusedReport.id);
//	}
//
//	@Test(dependsOnMethods = "createReport")
//	public void readReport() throws Exception {
//		try (ApiTester apiTester = apiTesterBuilder.build()) {
//			apiTester
//					.httpGet(reusedReport.id.toString())
//					.send();
//			apiTester
//					.isOk()
//					.matchContentType(MediaType.JSON_UTF_8);
//			Report report = apiTester.getContent(Report.class);
//			Assert.assertEquals(report.id, reusedReport.id);
//		}
//	}
//
//	@Test(dependsOnMethods = "readReport")
//	public void updateReport() throws Exception {
//		reusedReport.setName("Updated name");
//
//		try (ApiTester apiTester = apiTesterBuilder.build()) {
//			apiTester
//					.httpPut(reusedReport.id.toString(), reusedReport)
//					.send();
//			apiTester
//					.isOk()
//					.matchContentType(MediaType.JSON_UTF_8);
//			Report report = apiTester.getContent(Report.class);
//			Assert.assertNotNull(report);
//			Assert.assertEquals(report.getName(), reusedReport.getName());
//		}
//
//		Report report1 = Report.get(reusedReport.id);
//		Assert.assertEquals(report1.getName(), reusedReport.getName());
//	}
//
//	@Test(dependsOnMethods = "updateReport")
//	public void deleteReport() throws Exception {
//		try (ApiTester apiTester = apiTesterBuilder.build()) {
//			apiTester
//					.httpDelete(reusedReport.id.toString())
//					.send();
//			apiTester
//					.isOk()
//					.matchContentType(MediaType.JSON_UTF_8);
//		}
//
//		Report report1 = Report.get(reusedReport.id);
//		Assert.assertNull(report1);
//	}
//}
