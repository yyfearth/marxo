package marxo.entity.workflow;

import com.fasterxml.jackson.annotation.JsonIgnore;
import marxo.entity.user.RunnableEntity;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Transient;

public abstract class WorkflowChildEntity extends RunnableEntity {
	public ObjectId workflowId;
	@Transient
	@JsonIgnore
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
