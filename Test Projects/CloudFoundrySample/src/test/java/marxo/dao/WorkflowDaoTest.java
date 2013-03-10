package marxo.dao;

import junit.framework.Assert;
import marxo.Bean.Workflow;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class WorkflowDaoTest {
	WorkflowDao workflowDao;
	Workflow workflow;

	@BeforeClass
	public void setUp() {
		workflowDao = new WorkflowDao();
		workflowDao.removeAll();
	}

	@Test(groups = {"create"})
	public void testCreate() throws Exception {
		workflow = new Workflow();
		workflow.setName("test");

		boolean isOkay = workflowDao.create(workflow);

		Assert.assertTrue(isOkay);
	}

	@Test(groups = {"read"}, dependsOnGroups = {"create"})
	public void testRead() throws Exception {
//		Workflow workflow = workflowDao.read(this.workflow.getId());
//
//		Assert.assertEquals(this.workflow, workflow);
	}

	@Test(groups = {"update"}, dependsOnGroups = {"read"})
	public void testUpdate() throws Exception {
		Assert.fail();

	}

	@Test(groups = {"update"}, dependsOnGroups = {"read"})
	public void testCreateOrUpdate() throws Exception {
		Assert.fail();
	}

	@Test(groups = {"delete"}, dependsOnGroups = {"update"})
	public void testDelete() throws Exception {
		Assert.fail();

	}

	@Test(groups = {"search"}, dependsOnGroups = {"create"})
	public void testFindOne() throws Exception {
		Assert.fail();

	}

	@Test(groups = {"search"}, dependsOnGroups = {"create"})
	public void testFind() throws Exception {
		Assert.fail();

	}
}
