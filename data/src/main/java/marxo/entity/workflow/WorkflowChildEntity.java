package marxo.entity.workflow;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Transient;

public abstract class WorkflowChildEntity extends RunnableEntity {
	public ObjectId workflowId;
	@Transient
	protected Workflow workflow;

	public Workflow getWorkflow() {
		return workflow;
	}

	public void setWorkflow(Workflow workflow) {
		this.workflow = workflow;
		this.workflowId = workflow.id;
		this.tenantId = workflow.tenantId;
	}
}
