package marxo.entity.node;

import marxo.entity.content.Content;
import marxo.entity.user.TenantChildEntity;
import org.bson.types.ObjectId;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.MongoTemplate;

// todo: make this class abstract
public class Action extends TenantChildEntity {
	protected static final ApplicationContext applicationContext = new ClassPathXmlApplicationContext("mongo-configuration.xml");
	protected static final MongoTemplate mongoTemplate = applicationContext.getBean(MongoTemplate.class);
	public ObjectId nodeId;
	public String contentType;
	public ObjectId contentId;
	protected Event event;
	@Transient
	protected Node node;
	@Transient
	protected Content content;

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
		event.actionId = id;
	}

	public Content getContent() {
		return content;
	}

	public void setContent(Content content) {
		this.content = content;
		this.contentId = content.id;

		content.setAction(this);
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
		this.nodeId = node.id;
	}

	public void act() {
	}
}
