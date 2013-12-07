package marxo.entity.action;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.exception.FacebookException;
import com.restfb.types.FacebookType;
import com.restfb.types.Post;
import marxo.entity.node.Event;
import marxo.entity.node.NodeChildEntity;
import marxo.entity.workflow.Notification;
import marxo.entity.workflow.RunStatus;
import marxo.exception.Errors;
import org.bson.types.ObjectId;
import org.joda.time.Duration;
import org.joda.time.Period;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@Document(collection = "action")
public class Action extends NodeChildEntity {

	public Action(Type type) {
		setType(type);
	}

	public Action() {
		this(Type.DEFAULT);
	}

	/*
	Type
	 */

	public static enum Type {
		DEFAULT,
		FACEBOOK,
		TWITTER,
		EMAIL,
		PAGE,
		WAIT,
		TRIGGER,
	}

	protected Type type = Type.DEFAULT;

	public Type getType() {
		return type;
	}

	@Override
	public void wire() {
		if (content != null) {
			content.setAction(this);
		}
		super.wire();
	}

	public void setType(Type type) {
		this.type = type;

		switch (type) {
			case DEFAULT:
				break;
			case FACEBOOK:
				isTracked = true;

				if (monitorDuration == null) {
					monitorDuration = Duration.standardDays(1);
				}

				if (monitorPeriod == null) {
					monitorPeriod = Period.days(1);
				}

				break;
			case TWITTER:
				break;
			case EMAIL:
				break;
			case PAGE:
				break;
			case WAIT:
				break;
			case TRIGGER:
				break;
		}
	}

	/*
	Post
	 */

	@JsonProperty("tracked")
	public Boolean isTracked;

	public Duration monitorDuration;
	public Period monitorPeriod;

	/*
	Next action
	 */

	public ObjectId nextActionId;

	@Transient
	@JsonIgnore
	protected Action nextAction;

	public Action getNextAction() {
		if (nextActionId == null) {
			return nextAction = null;
		}
		return (nextAction == null) ? (nextAction = mongoTemplate.findById(nextActionId, Action.class)) : nextAction;
	}

	public void setNextAction(Action nextAction) {
		this.nextAction = nextAction;
		this.nextActionId = nextAction.id;
	}

	/*
	Event
	 */

	public ObjectId eventId;

	@Transient
	protected Event event;

	@JsonIgnore
	public Event getEvent() {
		if (eventId == null) {
			return null;
		}
		return (event == null) ? (event = mongoTemplate.findById(eventId, Event.class)) : event;
	}

	@JsonIgnore
	public void setEvent(Event event) {
		this.event = event;
		this.eventId = event.id;
		event.setAction(this);
	}

	/*
	Content
	 */

	public ObjectId contentId;
	@Transient
	protected Content content;
	public String contentType;

	public Content getContent() {
		if (contentId == null) {
			return null;
		}
		return (content == null) ? (content = Content.get(contentId)) : content;
	}

	public void setContent(Content content) {
		this.content = content;
		this.contentId = content.id;
		content.setAction(this);
	}

	/*
	Error
	 */

	protected Errors errors = new Errors();

	public Errors getErrors() {
		return errors;
	}

	/**
	 * @return whether it should continue.
	 */
	public boolean act() {
		switch (status) {
			case PAUSED:
			case STOPPED:
			case FINISHED:
			case ERROR:
			case WAITING:
				logger.debug(String.format("%s skips with status %s", this, status));
				break;
		}

		if (type.equals(Type.DEFAULT)) {
			errors.add("A default action is only for demo");
			status = RunStatus.ERROR;
			return false;
		}

		if (type.equals(Type.FACEBOOK)) {
			if (getTenant().facebookData == null || getTenant().facebookData.accessToken == null) {
				logger.info(String.format("%s doesn't have Facebook to continue", this));

				Notification notification = new Notification(Notification.Level.CRITICAL, "Please update your Facebook permission");
				notification.setTenant(getTenant());
				notification.save();
				status = RunStatus.ERROR;
				return false;
			}

			FacebookClient facebookClient = new DefaultFacebookClient(getTenant().facebookData.accessToken);

			try {
				if (getContent().getPostId() == null) {
					getContent().messageResponse = facebookClient.publish("me/feed", FacebookType.class, Parameter.with("message", getContent().message));
					content.save();
					logger.info(String.format("Submit Facebook post [%s]", content.messageResponse));

					if (isTracked) {
						status = RunStatus.FINISHED;
					} else {
						status = RunStatus.MONITORING;
					}
					return true;
				} else {
					Post post = facebookClient.fetchObject(getContent().getPostId(), Post.class);
					Content.FacebookRecord facebookRecord = Content.FacebookRecord.fromPost(post);
					getContent().records.add(facebookRecord);
					return true;
				}
			} catch (FacebookException e) {
				logger.info(String.format("%s [%s] %s", this, e.getClass().getSimpleName(), e.getMessage()));

				Notification notification = new Notification(Notification.Level.CRITICAL, "Please update the Facebook permission");
				notification.setTenant(getTenant());
				notification.save();
				status = RunStatus.ERROR;
				return false;
			}
		}

		errors.add("Unknown action type is discovered");
		status = RunStatus.ERROR;
		return false;
	}

	@Override
	public boolean validate(Errors errors) {
		if (nodeId == null) {
			errors.add(String.format("%s [%s] has no node", this, id));
		}

		return super.validate(errors);
	}

	/*
	DAO
	 */

	@Override
	public void save() {
		super.save();
		if (event != null) {
			event.save();
		}
		if (content != null) {
			content.save();
		}
	}

	public static Action get(ObjectId id) {
		return mongoTemplate.findById(id, Action.class);
	}

	public static List<Action> get(List<ObjectId> actionIds) {
		return mongoTemplate.find(Query.query(Criteria.where("_id").in(actionIds)), Action.class);
	}
}
