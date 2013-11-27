package marxo.entity.node;

import marxo.entity.BasicEntity;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.springframework.data.annotation.Transient;

public class Event extends BasicEntity {
	DateTime startTime;
	DateTime endTime;
	Duration duration;

	/*
	Action
	 */

	public ObjectId actionId;

	@Transient
	protected Action action;

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
		this.actionId = action.id;
	}

	/*
	Node
	 */

	public ObjectId nodeId;

	protected Node node;

	public Node getNode() {
		if (nodeId == null) {
			return node = null;
		}
		return (node == null) ? (node = mongoTemplate.findById(nodeId, Node.class)) : node;
	}

	public void setNode(Node node) {
		this.node = node;
		this.nodeId = node.id;
	}

	// Write logic in each setter.

	public DateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(DateTime startTime) {
		this.startTime = startTime;
	}

	public DateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(DateTime endTime) {
		this.endTime = endTime;
	}

	public Duration getDuration() {
		return duration;
	}

	public void setDuration(Duration duration) {
		this.duration = duration;
	}

	@Override
	public void save() {
		throw new UnsupportedOperationException(String.format("%s should not be saved into database", getClass().getSimpleName()));
	}
}
