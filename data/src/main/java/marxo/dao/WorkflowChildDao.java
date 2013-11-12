package marxo.dao;

import marxo.entity.WorkflowChildEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

public abstract class WorkflowChildDao<Entity extends WorkflowChildEntity> extends TenantChildDao<Entity> {
	@Override
	public Criteria getFilterCriteria() {
		return Criteria.where("tenantId").is(tenantId);
	}

	public List<Entity> searchByWorkflowIds(List<ObjectId> workflowIds) {
		return mongoTemplate.find(Query.query(Criteria.where("workflowId").in(workflowIds)), entityClass);
	}
}
