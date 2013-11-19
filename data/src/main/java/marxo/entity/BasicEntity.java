package marxo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Set;

// review: it's fucking weird that the entities are coupled with Jackson annotation.
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonPropertyOrder({"id", "email", "object_type", "tenant_id", "workflow_id", "name", "key", "desc", "context_type", "status", "content", "nodes", "node_ids", "links", "link_ids", "created_at", "created_by", "updated_at", "modified_by"})
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class BasicEntity {
	@Id
	@JsonProperty("id")
	public ObjectId id = new ObjectId();
	@Field(order = 4)
	public String key = id.toString();
	@Field(order = 50)
	@JsonProperty("created_at")
	public DateTime createdDate = DateTime.now();
	@Field(order = 52)
	@JsonProperty("updated_at")
	public DateTime modifiedDate = DateTime.now();
	@JsonProperty("created_by")
	@Field(order = 51)
	public ObjectId createdByUserId;
	@Field(order = 53)
	@JsonProperty("modified_by")
	public ObjectId modifiedByUserId;
	@JsonProperty("desc")
	public String description = "";
	@Field(order = 3)
	protected String name = "";
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

	@JsonIgnore
	public BasicEntity getCreatedCopy() throws IllegalAccessException, InstantiationException, NoSuchFieldException {
		Class<? extends BasicEntity> basicEntityClass = getClass();
		BasicEntity newEntity = basicEntityClass.newInstance();
		Set<String> strings = Sets.newHashSet(
				"name",
				"key",
				"description",
				"createdByUserId",
				"modifiedByUserId"
		);
		for (String fieldName : strings) {
			java.lang.reflect.Field field = basicEntityClass.getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(newEntity, field.get(this));
		}
		return newEntity;
	}
}
