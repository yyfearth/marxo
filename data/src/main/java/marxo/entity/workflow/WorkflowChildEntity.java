package marxo.entity.workflow;

import marxo.entity.user.TenantChildEntity;
import org.bson.types.ObjectId;

public abstract class WorkflowChildEntity extends TenantChildEntity {
	public ObjectId workflowId;
}
