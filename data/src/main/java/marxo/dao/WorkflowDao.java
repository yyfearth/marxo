package marxo.dao;

import marxo.entity.Workflow;
import org.springframework.stereotype.Repository;

@Repository
public class WorkflowDao extends TenantChildDao<Workflow> {
	public WorkflowDao() {
		super();
	}
}
