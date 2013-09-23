package marxo.dao;

import marxo.bean.Workflow;

public class WorkflowDao extends BasicDao<Workflow> {
	@Override
	public void save(Workflow entity) {
		// TODO: validate the workflow, to maintain the consistency of the data.
		mongoTemplate.save(entity);
	}
}
