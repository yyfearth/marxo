package marxo.entity.workflow;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;
import marxo.entity.BasicEntity;
import marxo.entity.action.Action;
import marxo.entity.action.ActionChildEntity;
import marxo.entity.link.Link;
import marxo.entity.node.Node;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.Weeks;

public class Notification extends ActionChildEntity implements Comparable<Notification> {

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

	@Override
	public int compareTo(Notification notification) {

		return -Integer.compare(this.level.value, notification.level.value);
	}

	//	public Class<? extends BasicEntity> targetClass;
	public static enum Level {
		MINOR(1),
		NORMAL(2),
		MAJOR(3),
		TRIVIAL(4),
		WARNING(5),
		CRITICAL(6),
		FATAL(7),
		ERROR(8),;

		int value;

		Level(int value) {
			this.value = value;
		}
	}

	public Level level = Level.NORMAL;

	@JsonProperty("expires_at")
	public DateTime expireTime = DateTime.now().plus(Weeks.ONE);

	public boolean isExpired() {
		return expireTime.isBeforeNow();
	}

	/*
	DAO
	 */

	public static enum Type {
		STARTED,
		TRACKED,
		FINISHED,
		FACEBOOK_TOKEN,
		WAIT_FOR_USER,
		ERROR,
	}

	public static Notification saveNew(Level level, BasicEntity entity, Type type) {
		Notification notification = new Notification(level);

		if (entity instanceof Workflow) {
			notification.setWorkflow((Workflow) entity);
		} else if (entity instanceof Node) {
			notification.setNode((Node) entity);
		} else if (entity instanceof Action) {
			notification.setAction((Action) entity);
		} else if (entity instanceof Link) {
			notification.setLink((Link) entity);
		} else {
			logger.error(String.format("No notification for such entity (%s)", entity));
			return null;
		}

		String formatString = "";

		switch (type) {
			case STARTED:
				formatString = "%s is started";
				break;
			case TRACKED:
				formatString = "%s is tracked";
				break;
			case FINISHED:
				formatString = "%s is finished";
				break;
			case FACEBOOK_TOKEN:
				formatString = "%s requires your Facebook permission";
				break;
			case WAIT_FOR_USER:
				formatString = "%s requires user action";
				break;
			case ERROR:
				formatString = "%s has error";
				break;
		}

		Class<? extends BasicEntity> aClass = entity.getClass();
		String entityApperance = aClass.getSimpleName() + Objects.firstNonNull(entity.getName(), "");
		notification.setName(String.format(formatString, entityApperance));
		notification.save();

		return notification;
	}

	public static Notification get(ObjectId id) {
		return mongoTemplate.findById(id, Notification.class);
	}


}