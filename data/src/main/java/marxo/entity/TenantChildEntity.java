package marxo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.types.ObjectId;

public class TenantChildEntity extends BasicEntity {
	@JsonIgnore
	public ObjectId tenantId;
	@JsonIgnore
	public ObjectId templateId;

	public String getJsonTemplateId() {
		return (templateId == null) ? null : templateId.toString();
	}

	public void setTemplateId(String templateId) {
		this.templateId = new ObjectId(templateId);
	}

	@JsonProperty("tenant_id")
	public String getJsonTenantId() {
		return (tenantId == null) ? null : tenantId.toString();
	}

	@JsonProperty("tenant_id")
	public void setJsonTenantId(String tenantId) {
		this.tenantId = (tenantId == null) ? null : new ObjectId(tenantId);
	}
}
