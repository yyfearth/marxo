package marxo.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import java.util.Date;

public abstract class BasicEntity {

	@Id
	@JsonIgnore
	ObjectId id;
	String name;
	String title;
	@JsonIgnore
	ObjectId createdByUserId;
	@JsonProperty("created")
	Date createdDate;
	@JsonIgnore
	ObjectId modifiedByUserId;
	@JsonProperty("modified")
	Date modifiedDate;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
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

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public ObjectId getModifiedByUserId() {
		return modifiedByUserId;
	}

	public void setModifiedByUserId(ObjectId modifiedByUserId) {
		this.modifiedByUserId = modifiedByUserId;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	////////// For JSON output
	@JsonProperty("id")
	public String getJsonId() {
		return (id == null) ? null : id.toString();
	}

	@JsonProperty("id")
	public void setJsonId(String id) {
		this.id = new ObjectId(id);
	}

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

	@JsonProperty("objectType")
	public String getObjectType() {
		Class<?> clazz = getClass();

		if (clazz.isAnonymousClass()) {
			clazz = clazz.getSuperclass();
		}

		return clazz.getSimpleName();
	}

}
