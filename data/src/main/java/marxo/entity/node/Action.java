package marxo.entity.node;

import com.fasterxml.jackson.annotation.JsonIgnore;
import marxo.entity.content.Content;
import marxo.entity.user.TenantChildEntity;
import marxo.entity.workflow.RunStatus;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Transient;

// todo: make this class abstract
public class Action extends TenantChildEntity {
	public RunStatus status = RunStatus.IDLE;

	protected Event event;
	public ObjectId contentId;

	@JsonIgnore
	public Event getEvent() {
		return event;
	}

	@JsonIgnore
	public void setEvent(Event event) {
		this.event = event;
		event.actionId = id;
	}

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

	public void act() {
	}
}
