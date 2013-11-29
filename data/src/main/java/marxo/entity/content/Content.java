package marxo.entity.content;

import marxo.entity.node.Action;
import marxo.entity.user.TenantChildEntity;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "content")
public class Content extends TenantChildEntity {
	public ObjectId actionId;
	@Transient
	protected Action action;

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
		this.actionId = action.id;
		this.tenantId = action.tenantId;
	}

	/*
	DAO
	 */

	public static Content get(ObjectId id) {
		return mongoTemplate.findById(id, Content.class);
	}
}
