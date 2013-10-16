package marxo.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import java.util.Date;

// review: it's fucking weird that the entities are coupled with Jackson annotation.
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonPropertyOrder({"id", "object_type", "tenant_id", "workflow_id", "name", "key", "desc", "type", "status", "content", "nodes", "node_ids", "links", "link_ids", "created_at", "created_by", "updated_at", "modified_by"})
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class BasicEntity {
	@Id
	@JsonIgnore
	public ObjectId id;
	public String name;
	@JsonProperty("created_at")
	public Date createdDate = new Date();
	@JsonProperty("updated_at")
	public Date modifiedDate = new Date();
	public String key;
	@JsonProperty("desc")
	public String description;
	@JsonIgnore
	public ObjectId createdByUserId;
	@JsonIgnore
	public ObjectId modifiedByUserId;

	@JsonProperty("id")
	public String getJsonId() {
		return (id == null) ? null : id.toString();
	}

	@JsonProperty("object_type")
	public String getObjectType() {
		Class<?> aClass = getClass();

		if (aClass.isAnonymousClass()) {
			aClass = aClass.getSuperclass();
		}

		return aClass.getSimpleName();
	}

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
		if (id == null) {
			id = new ObjectId();
		}

		if (name == null) {
			name = "";
		}

		if (createdDate == null) {
			createdDate = new Date();
		}

		if (modifiedDate == null) {
			modifiedDate = new Date();
		}

		if (key == null) {
			key = "";
		}
	}
}
