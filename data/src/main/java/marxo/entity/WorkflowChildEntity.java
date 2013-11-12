package marxo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.types.ObjectId;

public abstract class WorkflowChildEntity extends TenantChildEntity {
	@JsonProperty("workflow_id")
	public ObjectId workflowId;
}
