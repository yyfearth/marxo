package marxo.dao;

import marxo.entity.ProjectStatus;
import marxo.entity.Workflow;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

@Repository
public class WorkflowDao extends TenantChildDao<Workflow> {
	public WorkflowDao() {
		super();
	}

	/**
	 * Get next project which is waiting to be processed.
	 */
	public Workflow getNextProject() {
		Criteria criteria = Criteria.where("isProject").is(true).and("status").is(ProjectStatus.IDLE);
		return mongoTemplate.findOne(org.springframework.data.mongodb.core.query.Query.query(criteria), Workflow.class);
	}
}
