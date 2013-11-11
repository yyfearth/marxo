package marxo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.types.ObjectId;

public class TenantChildEntity extends BasicEntity {
	@JsonProperty("tenant_id")
	public ObjectId tenantId;
	@JsonProperty("template_id")
	public ObjectId templateId;
}
