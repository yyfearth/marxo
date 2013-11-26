package marxo.entity.content;

import marxo.entity.BasicEntity;
import marxo.entity.node.Action;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "content")
public class Content extends BasicEntity {
	public ObjectId actionId;
	@Transient
	protected Action action;

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
		this.actionId = action.id;
	}
}
