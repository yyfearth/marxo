package marxo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.types.ObjectId;

public class TenantChildEntity extends BasicEntity {
	public ObjectId tenantId;
	public ObjectId templateId;
}
