package marxo.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.types.ObjectId;

public abstract class BasicEntity extends Entity {
	public String name;
	public String title;
	public String key;
	@JsonProperty("desc")
	public String description;
	@JsonIgnore
	public ObjectId createdByUserId;
	@JsonIgnore
	public ObjectId modifiedByUserId;

	public BasicEntity() {
	}

	protected BasicEntity(String name, String title) {
		this.name = name;
		this.title = title;
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

//	public String getName() {
//		return name;
//	}
//
//	public void setName(String name) {
//		this.name = name;
//	}
//
//	public String getTitle() {
//		return title;
//	}
//
//	public void setTitle(String title) {
//		this.title = title;
//	}

	public ObjectId getModifiedByUserId() {
		return modifiedByUserId;
	}

	public void setModifiedByUserId(ObjectId modifiedByUserId) {
		this.modifiedByUserId = modifiedByUserId;
	}

	////////// For JSON output

	@JsonProperty("created_by")
	public String getJsonCreatedByUserId() {
		return (createdByUserId == null) ? null : createdByUserId.toString();
	}

	@JsonProperty("created_by")
	public void setJsonCreatedByUserId(String createdByUserId) {
		this.createdByUserId = new ObjectId(createdByUserId);
	}

	@JsonProperty("modified_by")
	public String getJsonModifiedByUserId() {
		return (modifiedByUserId == null) ? null : modifiedByUserId.toString();
	}

	@JsonProperty("modified_by")
	public void setJsonModifiedByUserId(String modifiedByUserId) {
		this.modifiedByUserId = new ObjectId(modifiedByUserId);
	}

	public void fillWithDefaultValues() {
		super.fillWithDefaultValues();

		if (name == null) {
			name = "";
		}

		if (title == null) {
			title = "";
		}
	}
}
