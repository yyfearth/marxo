package marxo.entity;

import org.bson.types.ObjectId;

enum TaskType {
	WORKFLOW,
}

public class Task extends BasicEntity {
	public TaskType type;
	public ObjectId workflowId;

	public Task(ObjectId workflowId) {
		this.workflowId = workflowId;
	}
}
