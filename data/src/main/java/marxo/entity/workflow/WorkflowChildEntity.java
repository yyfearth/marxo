package marxo.entity.workflow;

import marxo.entity.user.TenantChildEntity;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Transient;

public abstract class WorkflowChildEntity extends TenantChildEntity {
	public ObjectId workflowId;
	@Transient
	protected Workflow workflow;

	public Workflow getWorkflow() {
		return workflow;
	}

	public void setWorkflow(Workflow workflow) {
		this.workflow = workflow;
		this.workflowId = workflow.id;
	}
}
