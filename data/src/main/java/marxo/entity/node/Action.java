package marxo.entity.node;

import marxo.dao.ContentDao;
import marxo.entity.content.Content;
import marxo.entity.user.TenantChildEntity;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Transient;

// todo: make this class abstract
public class Action extends TenantChildEntity {
	public String eventType;
	public ObjectId eventId;
	public String contentType;
	public ObjectId contentId;
	public ObjectId nodeId;
	@Transient
	@Autowired
	protected ContentDao contentDao;
	@Transient
	protected Content content;
	@Transient
	protected Event event;
	@Transient
	protected Node node;

	public Content getContent() {
		return content;
	}

	public void setContent(Content content) {
		this.content = content;
		this.contentId = content.id;
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
		this.nodeId = node.id;
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
		this.eventId = event.id;
		this.eventType = event.getClass().getSimpleName();
	}

	public void act() {
	}
}
