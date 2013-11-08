package marxo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Strings;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.regex.Pattern;

// review: it's fucking weird that the entities are coupled with Jackson annotation.
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonPropertyOrder({"id", "email", "object_type", "tenant_id", "workflow_id", "name", "key", "desc", "type", "status", "content", "nodes", "node_ids", "links", "link_ids", "created_at", "created_by", "updated_at", "modified_by"})
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class BasicEntity {
	static Pattern pattern = Pattern.compile("");
	@Id
	@JsonIgnore
	public ObjectId id;
	@Field(order = 3)
	public String name;
	@Field(order = 4)
	public String key;
	@JsonIgnore
	@Field(order = 50)
	public Date createdDate = new Date();
	@JsonIgnore
	@Field(order = 51)
	public ObjectId createdByUserId;
	@JsonIgnore
	@Field(order = 52)
	public Date modifiedDate = new Date();
	@JsonIgnore
	@Field(order = 53)
	public ObjectId modifiedByUserId;
	@JsonProperty("desc")
	public String description;

	@JsonProperty("created_at")
	public String getCreatedDate() {
		return (new DateTime(createdDate)).toString();
	}

	@JsonProperty("created_at")
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	@JsonProperty("updated_at")
	public String getModifiedDate() {
		return (new DateTime(modifiedDate)).toString();
	}

	@JsonProperty("updated_at")
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

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
			if (!Strings.isNullOrEmpty(name)) {
				key = name.replaceAll("[^\\w]+", "_").toLowerCase();
			}
		}
	}
}
