package marxo.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Strings;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Field;

// review: it's fucking weird that the entities are coupled with Jackson annotation.
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonPropertyOrder({"id", "email", "object_type", "tenant_id", "workflow_id", "name", "key", "desc", "context_type", "status", "content", "nodes", "node_ids", "links", "link_ids", "created_at", "created_by", "updated_at", "modified_by"})
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class BasicEntity {
	@Id
	@JsonProperty("id")
	public ObjectId id;
	@Field(order = 4)
	public String key;
	@Field(order = 50)
	@JsonProperty("created_at")
	public DateTime createdDate;
	@Field(order = 52)
	@JsonProperty("updated_at")
	public DateTime modifiedDate;
	@JsonProperty("created_by")
	@Field(order = 51)
	public ObjectId createdByUserId;
	@Field(order = 53)
	@JsonProperty("modified_by")
	public ObjectId modifiedByUserId;
	@JsonProperty("desc")
	public String description;
	@Field(order = 3)
	protected String name;
	@Transient
	protected String objectType;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		if (!Strings.isNullOrEmpty(name) && key == null) {
			key = name.replaceAll("[^\\w]+", "_").toLowerCase();
		}
	}

	public String getObjectType() {
		if (objectType == null) {
			Class<?> aClass = getClass();
			if (aClass.isAnonymousClass()) {
				aClass = aClass.getSuperclass();
			}
			objectType = aClass.getSimpleName();
		}
		return objectType;
	}

	public void fillWithDefaultValues() {
		if (id == null) {
			id = new ObjectId();
		}

		if (name == null) {
			name = "";
		}

		if (key == null) {
			if (!Strings.isNullOrEmpty(name)) {
				key = name.replaceAll("[^\\w]+", "_").toLowerCase();
			}
		}

		if (createdDate == null) {
			createdDate = DateTime.now();
		}

		if (modifiedDate == null) {
			modifiedDate = DateTime.now();
		}
	}
}
