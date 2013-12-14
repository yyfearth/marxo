package marxo.entity.workflow;

import com.fasterxml.jackson.annotation.JsonProperty;
import marxo.entity.BasicEntity;
import marxo.entity.action.Action;
import marxo.entity.action.ActionChildEntity;
import marxo.entity.link.Link;
import marxo.entity.node.Node;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.Weeks;

public class Notification extends ActionChildEntity {

	public Notification(Level level, String messsage) {
		this.level = level;
		this.name = messsage;
	}

	public Notification(Level level) {
		this(level, "");
	}

	public Notification() {
		this(Level.MINOR, "");
	}

	public ObjectId linkId;
	protected Link link;

	public void setLink(Link link) {
		this.link = link;
		this.linkId = link.id;
		this.tenantId = link.tenantId;
		this.workflowId = link.workflowId;
	}

	public Link getLink() {
		if (linkId == null) {
			return link = null;
		}
		return mongoTemplate.findById(linkId, Link.class);
	}

	//	public Class<? extends BasicEntity> targetClass;
	public static enum Level {
		MINOR,
		NORMAL,
		MAJOR,
		TRIVIAL,
		WARNING,
		CRITICAL,
		FATAL,
		ERROR,
	}

	public Level level = Level.NORMAL;

	@JsonProperty("expires_at")
	public DateTime expireTime = DateTime.now().plus(Weeks.ONE);

	public boolean isExpired() {
		return expireTime.isAfterNow();
	}

	/*
	DAO
	 */

	public static Notification saveNew(Level level, BasicEntity entity, String message) {
		Notification notification = new Notification(level, message);

		Class<? extends BasicEntity> aClass = entity.getClass();
		if (entity instanceof Workflow) {
			notification.setWorkflow((Workflow) entity);
		} else if (entity instanceof Node) {
			notification.setNode((Node) entity);
		} else if (entity instanceof Action) {
			notification.setAction((Action) entity);
		} else if (entity instanceof Link) {
			notification.setLink((Link) entity);
		}

		notification.save();

		return notification;
	}

	public static Notification get(ObjectId id) {
		return mongoTemplate.findById(id, Notification.class);
	}
}