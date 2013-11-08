package marxo.engine;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class EngineTest {
	@BeforeMethod
	public void setUp() throws Exception {

	}

	@AfterMethod
	public void tearDown() throws Exception {

	}

	@Test
	public void testWorkerDirectly() throws Exception {
		EngineWorker engineWorker = new EngineWorker();
		engineWorker.run();
	}
}
