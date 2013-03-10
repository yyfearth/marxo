package marxo.Bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.jmkgreen.morphia.annotations.Id;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.UUID;

public class Workflow {
	public ObjectId get_id() {
		return _id;
	}

	public void set_id(ObjectId _id) {
		this._id = _id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public UUID[] getNodeIds() {
		return nodeIds;
	}

	public void setNodeIds(UUID[] nodeIds) {
		this.nodeIds = nodeIds;
	}

	public UUID[] getLinkIds() {
		return linkIds;
	}

	public void setLinkIds(UUID[] linkIds) {
		this.linkIds = linkIds;
	}

	public UUID getTenantId() {
		return tenantId;
	}

	public void setTenantId(UUID tenantId) {
		this.tenantId = tenantId;
	}

	public WorkflowType getType() {
		return type;
	}

	public void setType(WorkflowType type) {
		this.type = type;
	}

	public WorkflowStatus getStatus() {
		return status;
	}

	public void setStatus(WorkflowStatus status) {
		this.status = status;
	}

	public UUID getCreatedByUserId() {
		return createdByUserId;
	}

	public void setCreatedByUserId(UUID createdByUserId) {
		this.createdByUserId = createdByUserId;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public UUID getModifiedByUserId() {
		return modifiedByUserId;
	}

	public void setModifiedByUserId(UUID modifiedByUserId) {
		this.modifiedByUserId = modifiedByUserId;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	@Id
	@JsonIgnore
	ObjectId _id;
	String name;
	UUID[] nodeIds;
	UUID[] linkIds;
	UUID tenantId;
	WorkflowType type = WorkflowType.None;
	WorkflowStatus status = WorkflowStatus.None;
    @JsonProperty("createdBy")
	UUID createdByUserId;
    @JsonProperty("created")
	Date createdDate;
	UUID modifiedByUserId;
	Date modifiedDate;

	public Workflow() {
		this(new ObjectId());
	}

	public Workflow(ObjectId _id) {
		this._id = _id;
	}
}
