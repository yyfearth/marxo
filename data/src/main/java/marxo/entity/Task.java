package marxo.entity;

import org.bson.types.ObjectId;

enum TaskType {
	DEFAULT,
	WORKFLOW,
}

public class Task extends BasicEntity {
	public TaskType type = TaskType.DEFAULT;
	public ObjectId workflowId;

	public Task(ObjectId workflowId) {
		this.workflowId = workflowId;
	}
}
