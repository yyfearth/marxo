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
@JsonPropertyOrder({"id", "tenant_id", "name", "title", "desc", "type", "status", "nodes", "links", "created_at", "created_by", "updated_at", "modified_by", "object_type"})
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class Entity {
	@Id
	@JsonIgnore
	public ObjectId id;
	@JsonProperty("created_at")
	public Date createdDate = new Date();
	@JsonProperty("updated_at")
	public Date modifiedDate = new Date();

	public Entity() {
	}

//	public ObjectId getId() {
//		return id;
//	}
//
//	public void setId(ObjectId id) {
//		this.id = id;
//	}

//	public Date getCreatedDate() {
//		return createdDate;
//	}
//
//	public void setCreatedDate(Date createdDate) {
//		this.createdDate = createdDate;
//	}

//	public Date getModifiedDate() {
//		return modifiedDate;
//	}

//	public void setModifiedDate(Date modifiedDate) {
//		this.modifiedDate = modifiedDate;
//	}

	@JsonProperty("id")
	public String getJsonId() {
		return (id == null) ? null : id.toString();
	}

	@JsonProperty("id")
	public void setJsonId(String id) {
		this.id = new ObjectId(id);
	}

	@JsonProperty("object_type")
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

		if (createdDate == null) {
			createdDate = new Date();
		}

		if (modifiedDate == null) {
			modifiedDate = new Date();
		}
	}
}
