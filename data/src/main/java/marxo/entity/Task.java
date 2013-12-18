package marxo.entity;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

enum TaskType {
	DEFAULT,
	WORKFLOW,
}

public class Task extends BasicEntity {
	public TaskType type = TaskType.DEFAULT;
	public ObjectId workflowId;
	public DateTime time = DateTime.now();

	public Task(ObjectId workflowId) {
		this.workflowId = workflowId;
	}

	/*
	DAO
	 */

	public static Task get(ObjectId id) {
		return mongoTemplate.findById(id, Task.class);
	}

	/**
	 * Find and remove the next task from database.
	 */
	public static Task next() {
		Criteria criteria = Criteria.where("time").lte(DateTime.now());
		Query query = Query.query(criteria);
		return mongoTemplate.findAndRemove(query, Task.class);
	}

	public static Task schedule(ObjectId workdflowId, DateTime time) {
		Criteria workflowIdCriteria = Criteria.where("workflowId").is(workdflowId);
		Update update = Update.update("time", time);
		Task task;

		if (mongoTemplate.exists(Query.query(workflowIdCriteria), Task.class)) {
			task = mongoTemplate.findAndModify(Query.query(workflowIdCriteria.and("time").gt(time)), update, Task.class);
		} else {
			task = new Task(workdflowId);
			task.time = time;
			task.save();
		}

		return task;
	}

	public static Task schedule(ObjectId workdflowId) {
		return schedule(workdflowId, DateTime.now());
	}

	public static boolean workflowExists(ObjectId workdflowId) {
		return mongoTemplate.exists(Query.query(Criteria.where("workflowId").is(workdflowId)), Task.class);
	}

	public static long count() {
		return mongoTemplate.count(new Query(), Task.class);
	}

	@Override
	public void save() {
		super.save();
	}
}
