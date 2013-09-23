package marxo.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.bson.types.ObjectId;

public abstract class BasicEntity extends Entity {

	String name;
	String title;
	@JsonIgnore
	ObjectId createdByUserId;
	@JsonIgnore
	ObjectId modifiedByUserId;

	public BasicEntity() {
	}

	protected BasicEntity(String name, String title, ObjectId createdByUserId, ObjectId modifiedByUserId) {
		this.name = name;
		this.title = title;
		this.createdByUserId = createdByUserId;
		this.modifiedByUserId = modifiedByUserId;
	}

	public ObjectId getCreatedByUserId() {
		return createdByUserId;
	}

	public void setCreatedByUserId(ObjectId createdByUserId) {
		this.createdByUserId = createdByUserId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public ObjectId getModifiedByUserId() {
		return modifiedByUserId;
	}

	public void setModifiedByUserId(ObjectId modifiedByUserId) {
		this.modifiedByUserId = modifiedByUserId;
	}

	////////// For JSON output

	@JsonProperty("createdBy")
	public String getJsonCreatedByUserId() {
		return (createdByUserId == null) ? null : createdByUserId.toString();
	}

	@JsonProperty("createdBy")
	public void setJsonCreatedByUserId(String createdByUserId) {
		this.createdByUserId = new ObjectId(createdByUserId);
	}

	@JsonProperty("modifiedBy")
	public String getJsonModifiedByUserId() {
		return (modifiedByUserId == null) ? null : modifiedByUserId.toString();
	}

	@JsonProperty("modifiedBy")
	public void setJsonModifiedByUserId(String modifiedByUserId) {
		this.modifiedByUserId = new ObjectId(modifiedByUserId);
	}
}
