package marxo.dao;

import marxo.entity.WorkflowChildEntity;
import org.bson.types.ObjectId;

import java.util.List;

public abstract class WorkflowChildDao<Entity extends WorkflowChildEntity> extends TenantChildDao<Entity> {
	final public ObjectId workflowId;

	public WorkflowChildDao(ObjectId workflowId, ObjectId tenantId) {
		super(tenantId);
		this.workflowId = workflowId;
	}

	public WorkflowChildDao(ObjectId tenantId) {
		this(null, tenantId);
	}

	@Override
	protected void processEntity(Entity entity) {
		if (workflowId != null) {
			entity.workflowId = workflowId;
		}
		super.processEntity(entity);
	}

	@Override
	protected void processDataPairs(List<DataPair> dataPairs) {
		if (workflowId != null) {
			dataPairs.add(new DataPair("workflowId", workflowId));
		}
		super.processDataPairs(dataPairs);
	}
}
