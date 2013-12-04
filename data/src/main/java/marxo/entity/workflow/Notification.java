package marxo.entity.workflow;

import com.fasterxml.jackson.annotation.JsonProperty;
import marxo.entity.action.ActionChildEntity;
import marxo.entity.link.Link;
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

	//	public Notification(Type type, Class<? extends BasicEntity> targetClass, DateTime expireTime) {
//		this.type = type;
//		this.targetClass = targetClass;
//		this.expireTime = expireTime;
//	}
//
//	public Notification(Type type, Class<? extends BasicEntity> targetClass) {
//		DateTime defaultTime;
//		switch (type) {
//			case NEED_ATTENTION:
//				defaultTime = DateTime.now().plus(Weeks.ONE);
//				break;
//			default:
//				defaultTime = DateTime.now().plus(Weeks.ONE);
//				break;
//		}
//
//		this.type = type;
//		this.targetClass = targetClass;
//		this.expireTime = defaultTime;
//	}

//	@Override
//	public String getName() {
//		StringBuilder stringBuilder = new StringBuilder();
//
//		if (targetClass.equals(Action.class)) {
//			stringBuilder.append("Action");
//		} else if (targetClass.equals(Node.class)) {
//			stringBuilder.append("Node");
//		} else if (targetClass.equals(Workflow.class)) {
//			stringBuilder.append("Project");
//		} else {
//			throw new NotImplementedException();
//		}
//
//		switch (type) {
//			case STARTED:
//				stringBuilder.append(" started");
//				break;
//			case FINISHED:
//				stringBuilder.append(" finished");
//				break;
//			case NEED_ATTENTION:
//				stringBuilder.append(" requires user action");
//				break;
//			default:
//				throw new NotImplementedException();
//		}
//
//		return stringBuilder.toString();
//	}

//	public static enum Type {
//		STARTED,
//		FINISHED,
//		NEED_ATTENTION,
//	}
//
//	public Type type;

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

	public static Notification get(ObjectId id) {
		return mongoTemplate.findById(id, Notification.class);
	}
}