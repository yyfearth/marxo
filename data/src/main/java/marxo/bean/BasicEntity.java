package marxo.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.types.ObjectId;

public abstract class BasicEntity extends Entity {
	public String key;
	@JsonProperty("desc")
	public String description;
	@JsonIgnore
	public ObjectId createdByUserId;
	@JsonIgnore
	public ObjectId modifiedByUserId;

	public ObjectId getCreatedByUserId() {
		return createdByUserId;
	}

	public ObjectId getModifiedByUserId() {
		return modifiedByUserId;
	}

	@JsonProperty("created_by")
	public String getJsonCreatedByUserId() {
		return (createdByUserId == null) ? null : createdByUserId.toString();
	}

	@JsonProperty("modified_by")
	public String getJsonModifiedByUserId() {
		return (modifiedByUserId == null) ? null : modifiedByUserId.toString();
	}

	public void fillWithDefaultValues() {
		super.fillWithDefaultValues();

		if (key == null) {
			key = "";
		}
	}
}
