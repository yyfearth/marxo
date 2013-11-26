package marxo.entity.node;

import com.fasterxml.jackson.annotation.JsonIgnore;
import marxo.entity.content.Content;
import marxo.entity.user.TenantChildEntity;
import marxo.entity.workflow.RunStatus;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

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

	protected Event event;

	@JsonIgnore
	public Event getEvent() {
		return event;
	}

	@JsonIgnore
	public void setEvent(Event event) {
		this.event = event;
		event.actionId = id;
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
		return (content == null) ? (content = mongoTemplate.findById(this.contentId, Content.class)) : content;
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
	}

	/**
	 * @return true if the action is successfully processed.
	 */
	public boolean act() {
		return false;
	}

	@Override
	public void save() {
		throw new UnsupportedOperationException(String.format("%s should not be saved into database", getClass().getSimpleName()));
	}
}
