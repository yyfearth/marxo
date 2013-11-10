package marxo.dao;

import marxo.entity.WorkflowChildEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

public abstract class WorkflowChildDao<Entity extends WorkflowChildEntity> extends TenantChildDao<Entity> {
	ObjectId workflowId;

	public ObjectId getWorkflowId() {
		return new ObjectId(workflowId.toByteArray());
	}

	public void setWorkflowId(ObjectId workflowId) {
		this.workflowId = workflowId;
	}

	@Override
	public Criteria getFilterCriteria() {
		return Criteria.where("tenantId").is(tenantId);
	}

	@Override
	public void insert(Entity entity) {
		entity.workflowId = workflowId;
		super.insert(entity);
	}

	@Override
	public void insert(List<Entity> entities) {
		for (Entity entity : entities) {
			entity.workflowId = workflowId;
		}
		super.insert(entities);
	}

	@Override
	public void save(Entity entity) {
		entity.workflowId = workflowId;
		super.save(entity);
	}

	public List<Entity> searchByWorkflowIds(List<ObjectId> workflowIds) {
		return mongoTemplate.find(Query.query(Criteria.where("workflowId").in(workflowIds)), entityClass);
	}
}
