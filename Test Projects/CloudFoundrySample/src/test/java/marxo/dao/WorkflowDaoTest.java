//package marxo.dao;
//
//import junit.framework.Assert;
//import marxo.bean.Workflow;
//import marxo.data.JsonParser;
//import marxo.tool.DataGenerator;
//import org.testng.annotations.BeforeClass;
//import org.testng.annotations.Test;
//
//import java.util.ArrayList;
//import java.util.Date;
//
//public class WorkflowDaoTest {
//	WorkflowDao workflowDao;
//	ArrayList<Workflow> workflowList = new ArrayList<Workflow>();
//	String name;
//
//	@BeforeClass
//	public void setUp() {
//		workflowDao = new WorkflowDao();
//		workflowDao.removeAll();
//		name = DataGenerator.getRandomProjectName();
//	}
//
//	@Test(groups = {"create"})
//	public void testCreate() throws Exception {
//		for (int i = 0; i < 2; i++) {
//			Workflow workflow = new Workflow();
//			workflow.setName(name);
//			workflow.setModifiedDate(new Date());
//			workflow.setCreatedDate(new Date());
//
//			boolean isOkay = workflowDao.create(workflow);
//
//			Assert.assertTrue(isOkay);
//
//			workflowList.add(workflow);
//		}
//	}
//
//	@Test(groups = {"read"}, dependsOnGroups = {"create"})
//	public void testRead() throws Exception {
//		Workflow oldWordflow = workflowList.get(workflowList.size() - 1);
//		Workflow workflow = workflowDao.read(oldWordflow.getId());
//
//		assert oldWordflow.equals(workflow);
//	}
//
//	@Test(groups = {"update"}, dependsOnGroups = {"read"})
//	public void testUpdate() throws Exception {
//		Workflow oldWordflow = workflowList.get(workflowList.size() - 1);
//		Workflow readWorkflow = workflowDao.read(oldWordflow.getId());
//		name = DataGenerator.getRandomProjectName();
//		readWorkflow.setName(name);
//
//		assert workflowDao.update(readWorkflow);
//
//		readWorkflow = workflowDao.read(oldWordflow.getId());
//
//		assert readWorkflow.getName().equals(name);
//	}
//
//	@Test(groups = {"delete"}, dependsOnGroups = {"update"})
//	public void testDelete() throws Exception {
//		for (Workflow w : workflowList) {
//			assert workflowDao.delete(w.getId());
//		}
//	}
//
//	@Test(groups = {"parse"}, dependsOnGroups = {"create"})
//	public void testParse() throws Exception {
//		for (int i = 0; i < workflowList.size(); i++) {
//			System.out.println(JsonParser.getMapper().writeValueAsString(workflowList.get(i)));
//		}
//
//		assert true;
//	}
//
//	@Test(groups = {"search"}, dependsOnGroups = {"create"})
//	public void testFindOne() throws Exception {
//		Workflow wordflow = workflowList.get(0);
//		Workflow receivedWordflow = workflowDao.findOne("name", wordflow.getName());
//
//		assert receivedWordflow.equals(wordflow);
//	}
//
//	@Test(groups = {"search"}, dependsOnGroups = {"create"})
//	public void testFind() throws Exception {
//		Workflow[] receivedWordflows = workflowDao.find("name", name);
//
//		Assert.assertEquals(receivedWordflows.length, workflowList.size());
//
//		for (int i = 0; i < receivedWordflows.length; i++) {
//			Assert.assertEquals(workflowList.get(i), receivedWordflows[i]);
//		}
//	}
//}
