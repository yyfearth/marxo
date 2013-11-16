package marxo.entity.action;

import com.fasterxml.jackson.annotation.JsonProperty;
import marxo.entity.BasicEntity;
import org.bson.types.ObjectId;

public class Context extends BasicEntity {
	@JsonProperty("action_id")
	public ObjectId actionId;
}
