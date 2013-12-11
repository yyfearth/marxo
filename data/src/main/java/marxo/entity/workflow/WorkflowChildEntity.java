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
		if (workflowId == null) {
			return workflow = null;
		}
		return (workflow == null) ? (workflow = mongoTemplate.findById(workflowId, Workflow.class)) : workflow;
	}

	public void setWorkflow(Workflow workflow) {
		this.workflow = workflow;
		this.workflowId = workflow.id;
		this.tenantId = workflow.tenantId;
	}
}
