package marxo.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.types.ObjectId;

public class Action extends TenantChildEntity {
	public String type;
	public String content;
	@JsonProperty("context_id")
	public ObjectId contextId = new ObjectId();

	@Override
	public void fillWithDefaultValues() {
		super.fillWithDefaultValues();

		if (type == null) {
			type = "None";
		}

		if (content == null) {
			content = "";
		}
	}
}
