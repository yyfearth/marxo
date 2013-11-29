package marxo.entity.node;

import marxo.entity.user.TenantChildEntity;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.springframework.data.annotation.Transient;

public class Event extends TenantChildEntity {
	protected DateTime startTime;
	protected DateTime endTime;
	protected Duration duration;

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
		this.nodeId = action.nodeId;
		this.tenantId = action.tenantId;
	}

	/*
	Node
	 */

	public ObjectId nodeId;

	@Transient
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
		this.tenantId = node.tenantId;
	}

	public DateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(DateTime startTime) {
		this.startTime = startTime;
		if (endTime != null) {
			duration = new Duration(startTime.getMillis(), endTime.getMillis());
		} else if (duration != null) {
			endTime = startTime.plus(duration);
		}
	}

	public DateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(DateTime endTime) {
		this.endTime = endTime;
		if (startTime != null) {
			duration = new Duration(startTime.getMillis(), endTime.getMillis());
		}
	}

	public Duration getDuration() {
		return duration;
	}

	public void setDuration(Duration duration) {
		this.duration = duration;
		if (startTime != null) {
			endTime = startTime.plus(duration);
		}
	}

	public static Event get(ObjectId id) {
		return mongoTemplate.findById(id, Event.class);
	}
}
