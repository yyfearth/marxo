package marxo.entity.action;

import com.fasterxml.jackson.annotation.JsonIgnore;
import marxo.entity.content.Content;
import marxo.entity.node.Event;
import marxo.entity.node.NodeChildEntity;
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

	public ActionType type = ActionType.DEFAULT;

	/*
	Post
	 */

	public String postId;
	public boolean isTracked = true;

	public Duration monitorDuration = Duration.standardDays(1);
	public Period monitorPeriod = Period.days(1);

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

	public boolean act() {
		return true;
	}

	@Override
	public boolean validate(Errors errors) {
		if (nodeId == null) {
			errors.add(String.format("%s [%s] has no node", this, id));
		}

		return super.validate(errors);
	}

	@Override
	public void save() {
		mongoTemplate.save(this);
	}

	/*
	DAO
	 */

	public static Action get(ObjectId id) {
		return mongoTemplate.findById(id, Action.class);
	}

	public static List<Action> get(List<ObjectId> actionIds) {
		return mongoTemplate.find(Query.query(Criteria.where("_id").in(actionIds)), Action.class);
	}
}
