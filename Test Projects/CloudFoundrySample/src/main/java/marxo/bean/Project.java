package marxo.bean;

import com.github.jmkgreen.morphia.annotations.Id;

import java.util.Date;
import java.util.UUID;

//@JsonIgnoreProperties(value = {"_id"})
public class Project {

//	public ObjectId get_id() {
//		return _id;
//	}
//
//	public void set_id(ObjectId _id) {
//		this._id = _id;
//	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public UUID getTenantId() {
		return tenantId;
	}

	public void setTenantId(UUID tenantId) {
		this.tenantId = tenantId;
	}

	public UUID getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(UUID workflowId) {
		this.workflowId = workflowId;
	}

	public String getContextKey() {
		return contextKey;
	}

	public void setContextKey(String contextKey) {
		this.contextKey = contextKey;
	}

	public ProjectType getType() {
		return type;
	}

	public void setType(ProjectType type) {
		this.type = type;
	}

	public ProjectStatus getStatus() {
		return status;
	}

	public void setStatus(ProjectStatus status) {
		this.status = status;
	}

	public UUID getCreatedByUserId() {
		return createdByUserId;
	}

	public void setCreatedByUserId(UUID createdByUserId) {
		this.createdByUserId = createdByUserId;
	}

	public UUID getModifiedUserId() {
		return modifiedUserId;
	}

	public void setModifiedUserId(UUID modifiedUserId) {
		this.modifiedUserId = modifiedUserId;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	@Id
	public UUID id;
	public String name;
	public UUID tenantId;
	public UUID workflowId;
	String contextKey;
	ProjectType type = ProjectType.NONE;
	ProjectStatus status = ProjectStatus.NONE;
	public UUID createdByUserId;
	public UUID modifiedUserId;
	public Date createdDate = new Date();
	public Date modifiedDate = new Date();

	//////////
	public static final Project EMPTY_PROJECT;

	static {
		EMPTY_PROJECT = new Project(null);
		EMPTY_PROJECT.type = null;
		EMPTY_PROJECT.status = null;
	}

	public Project() {
		this(UUID.randomUUID());
	}

	public Project(UUID id) {
		this.id = id;
	}

	// It might be useful to generate equalTo and hashCode methods since we might want to compare two projects logically.
}
