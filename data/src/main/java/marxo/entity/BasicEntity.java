package marxo.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Strings;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.regex.Pattern;

// review: it's fucking weird that the entities are coupled with Jackson annotation.
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonPropertyOrder({"id", "email", "object_type", "tenant_id", "workflow_id", "name", "key", "desc", "context_type", "status", "content", "nodes", "node_ids", "links", "link_ids", "created_at", "created_by", "updated_at", "modified_by"})
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class BasicEntity {
	static Pattern pattern = Pattern.compile("");
	@Field(order = 3)
	public String name;
	@Id
	@JsonProperty("id")
//	@JsonIgnore
	public ObjectId id;
	@Field(order = 4)
	public String key;
	//	@JsonIgnore
	@Field(order = 50)
	@JsonProperty("created_at")
	public DateTime createdDate;
	//	@JsonIgnore
	@Field(order = 52)
	@JsonProperty("updated_at")
	public DateTime modifiedDate;
	//	@JsonIgnore
	@JsonProperty("created_by")
	@Field(order = 51)
	public ObjectId createdByUserId;
	//	@JsonIgnore
	@Field(order = 53)
	@JsonProperty("modified_by")
	public ObjectId modifiedByUserId;
	@JsonProperty("desc")
	public String description;

//	@JsonProperty("modified_by")
//	public ObjectId getModifiedByUserId() {
//		return modifiedByUserId;
//	}
//
//	@JsonProperty("updated_at")
//	public DateTime getModifiedDate() {
//		return modifiedDate;
//	}
//
//	@JsonProperty("created_at")
//	public DateTime getCreatedDate() {
//		return createdDate;
//	}

//	@JsonProperty("id")
//	public ObjectId getId() {
//		return id;
//	}

//	@JsonProperty("created_by")
//	public String getJsonCreatedByUserId() {
//		return (createdByUserId == null) ? null : createdByUserId.toString();
//	}

	public String getObjectType() {
		Class<?> aClass = getClass();

		if (aClass.isAnonymousClass()) {
			aClass = aClass.getSuperclass();
		}

		return aClass.getSimpleName();
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
