package marxo.entity.action;

import com.fasterxml.jackson.annotation.JsonProperty;
import marxo.entity.TenantChildEntity;
import org.bson.types.ObjectId;

public class Action extends TenantChildEntity {
	@JsonProperty("context_type")
	public ContextType contextType;
	@JsonProperty("context_id")
	public ObjectId contextId = new ObjectId();

	@Override
	public void fillWithDefaultValues() {
		super.fillWithDefaultValues();
	}
}
