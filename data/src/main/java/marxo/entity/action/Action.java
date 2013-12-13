package marxo.entity.action;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.exception.FacebookException;
import com.restfb.types.FacebookType;
import com.restfb.types.Post;
import marxo.entity.Task;
import marxo.entity.node.Event;
import marxo.entity.node.NodeChildEntity;
import marxo.entity.workflow.Notification;
import marxo.entity.workflow.RunStatus;
import marxo.exception.Errors;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Period;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@JsonTypeInfo(
		use = JsonTypeInfo.Id.NAME,
		include = JsonTypeInfo.As.PROPERTY,
//		defaultImpl = MonitorableAction.class,
		property = "type",
		visible = true
)
@JsonSubTypes({
		@JsonSubTypes.Type(value = FacebookAction.class, name = "FACEBOOK"),
		@JsonSubTypes.Type(value = TwitterAction.class, name = "TWITTER"),
		@JsonSubTypes.Type(value = EmailAction.class, name = "EMAIL"),
		@JsonSubTypes.Type(value = PageAction.class, name = "PAGE"),
		@JsonSubTypes.Type(value = WaitAction.class, name = "WAIT"),
		@JsonSubTypes.Type(value = TriggerAction.class, name = "TRIGGER"),
})
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
		return Type.DEFAULT;
	}

	@Override
	public void wire() {
		if (content != null) {
			content.setAction(this);
		}
		if (event != null) {
			event.setAction(this);
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

	public Errors errors = new Errors();

	/**
	 * @return whether it should continue.
	 */
	public boolean act() {
		boolean result = onAct();
		save();
		return result;
	}

	protected boolean onAct() {
		switch (status) {
			case FINISHED:
				return true;
			case PAUSED:
			case STOPPED:
			case ERROR:
			case WAITING:
				logger.info(String.format("%s doesn't continue because status is [%s]", this, status));
				return false;
		}

		if (type.equals(Type.DEFAULT)) {
			String message = String.format("%s has type [%s]", this, type);
			logger.error(message);
			errors.add(message);
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

					getEvent();
					if (isTracked && event != null) {
						status = RunStatus.MONITORING;

						getWorkflow().addTracableAction(this);
						getWorkflow().save();

						Notification notification = new Notification(Notification.Level.NORMAL, "Start monitoring");
						notification.setAction(this);
						notification.save();

						getEvent();
						if (event.getStartTime() == null) {
							event.setStartTime(DateTime.now());
							event.save();
						}
						Task.reschedule(workflowId, event.getStartTime());
					} else {
						status = RunStatus.FINISHED;
					}

					return true;
				} else {
					if (!isTracked || getEvent() == null) {
						status = RunStatus.FINISHED;
						return true;
					}


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
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}

		if (type.equals(Type.PAGE)) {
			getEvent();

			if (event == null) {
				Event event1 = new Event();
				setEvent(event1);
			}

			if (event.getStartTime() == null) {
				event.setStartTime(DateTime.now());
				event.save();
			}

			if (event.getStartTime().isBeforeNow()) {
				status = RunStatus.STARTED;

				Notification notification = new Notification(Notification.Level.NORMAL, "Page is posted");
				notification.setAction(this);
				notification.save();

				getWorkflow().addTracableAction(this);
				workflow.save();

				return true;
			}

			if (event.getEndTime().isBeforeNow()) {
				status = RunStatus.FINISHED;

				Notification notification = new Notification(Notification.Level.NORMAL, "Page is finished");
				notification.setAction(this);
				notification.save();

				return true;
			}

			Task.reschedule(workflowId, event.getStartTime());

			return false;
		}

		String message = String.format("%s type [%s] is not implemented", this, type);
		logger.info(message);
		errors.add(message);
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

	@Override
	public void remove() {
		if (contentId != null) {
			Content.remove(contentId);
		}

		if (eventId != null) {
			Event.remove(eventId);
		}

		super.remove();
	}

	public static Action get(ObjectId id) {
		return mongoTemplate.findById(id, Action.class);
	}

	public static void remove(ObjectId id) {
		mongoTemplate.findAndRemove(Query.query(Criteria.where("_id").is(id)), Action.class);
	}

	public static List<Action> get(List<ObjectId> actionIds) {
		return mongoTemplate.find(Query.query(Criteria.where("_id").in(actionIds)), Action.class);
	}
}
