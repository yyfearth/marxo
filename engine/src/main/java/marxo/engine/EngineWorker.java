package marxo.engine;

import marxo.dao.WorkflowDao;
import org.springframework.beans.factory.annotation.Autowired;

public class EngineWorker implements Runnable {
	@Autowired
	WorkflowDao workflowDao;

	@Override
	public void run() {

	}
}
