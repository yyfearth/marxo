package marxo.entity;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Query;

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

	public static Task get(ObjectId id) {
		return mongoTemplate.findById(id, Task.class);
	}

	public static Task findAndRemove() {
		return mongoTemplate.findAndRemove(new Query().with(modifiedTimeSort), Task.class);
	}

	public static long count() {
		return mongoTemplate.count(new Query(), Task.class);
	}
}
