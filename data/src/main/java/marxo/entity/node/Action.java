package marxo.entity.node;

import marxo.entity.content.Content;
import marxo.entity.user.TenantChildEntity;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Transient;

public abstract class Action extends TenantChildEntity {
	public String eventType;
	public ObjectId eventId;
	public String contentType;
	public ObjectId contentId;
	@Transient
	public Content content;
	@Transient
	protected Event event;

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
		this.eventId = event.id;
		this.eventType = event.getClass().getSimpleName();
	}

	public abstract void act();

	@Override
	public void fillWithDefaultValues() {
		super.fillWithDefaultValues();
	}
}
