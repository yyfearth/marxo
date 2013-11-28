package marxo.entity.node;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mongodb.WriteResult;
import marxo.entity.content.Content;
import marxo.entity.user.TenantChildEntity;
import marxo.exception.DatabaseException;
import marxo.exception.ValidationException;
import marxo.validation.Errors;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

// todo: make this class abstract
public class Action extends TenantChildEntity {
	public ObjectId nextActionId;

	@Transient
	@JsonIgnore
	protected Action nextAction;

	public Action getNextAction() {
		return nextAction;
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

	public ObjectId contentId;
	@Transient
	protected Content content;
	public String contentType;

	@JsonIgnore
	public Content getContent() {
		if (contentId == null) {
			return null;
		}
		return (content == null) ? (content = Content.get(contentId)) : content;
	}

	@JsonIgnore
	public void setContent(Content content) {
		this.content = content;
		this.contentId = content.id;
		content.setAction(this);
	}

	public ObjectId nodeId;
	@Transient
	protected Node node;

	@JsonIgnore
	public Node getNode() {
		if (nodeId == null) {
			return null;
		}
		return (node == null) ? (node = mongoTemplate.findById(this.nodeId, Node.class)) : node;
	}

	@JsonIgnore
	public void setNode(Node node) {
		this.node = node;
		this.nodeId = node.id;
		this.tenantId = node.tenantId;
	}

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

		if (contentId == null) {
			errors.add(String.format("%s [%s] has no content", this, id));
		}

		return super.validate(errors);
	}

	@Override
	public void save() {
		Errors errors = new Errors();
		if (!validate(errors)) {
			throw new ValidationException(errors.getMessages());
		}

		Criteria criteria = Criteria.where("_id").is(nodeId).and("actions").elemMatch(Criteria.where("_id").is(id));
		Update update = Update.update("actions.$", this);
		WriteResult writeResult = mongoTemplate.updateFirst(Query.query(criteria), update, Node.class);
		if (writeResult.getError() != null) {
			throw new DatabaseException(writeResult.getError());
		}
	}

	public static Action get(ObjectId id) {
		Criteria criteria = new Criteria().elemMatch(Criteria.where("_id").is(id));
		Node node = mongoTemplate.findOne(Query.query(criteria), Node.class);
		return (node == null) ? null : node.getActions().get(0);
	}
}
