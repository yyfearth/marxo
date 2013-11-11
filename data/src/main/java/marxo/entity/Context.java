package marxo.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.types.ObjectId;

public class Context extends BasicEntity {
	@JsonProperty("action_id")
	public ObjectId actionId;
}
