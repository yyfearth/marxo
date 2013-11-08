package marxo.dao;

import marxo.entity.WorkflowChildEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;

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
		return Criteria.where("tenantId").is(tenantId).and("workflowId").is(workflowId);
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
}
