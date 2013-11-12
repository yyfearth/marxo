package marxo.engine;

import marxo.dao.WorkflowDao;
import marxo.entity.Workflow;
import org.springframework.beans.factory.annotation.Autowired;

public class EngineWorker implements Runnable {
	@Autowired
	WorkflowDao workflowDao;

	// todo: make the method thread-safe, ready for multi-thread.
	@Override
	public void run() {
		Workflow project = workflowDao.getNextProject();
	}
}
