package marxo.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import java.util.Date;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonPropertyOrder({"id", "tenantId", "name", "title", "desc", "type", "status", "nodes", "links", "created", "createdBy", "modified", "modifiedBy", "objectType"})
public abstract class Entity {

	@Id
	@JsonIgnore
	ObjectId id;
	@JsonProperty("created")
	Date createdDate;
	@JsonProperty("modified")
	Date modifiedDate;

	public Entity() {
		id = new ObjectId();
		createdDate = new Date();
		modifiedDate = new Date();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	@JsonProperty("id")
	public String getJsonId() {
		return (id == null) ? null : id.toString();
	}

	@JsonProperty("id")
	public void setJsonId(String id) {
		this.id = new ObjectId(id);
	}

	@JsonProperty("objectType")
	public String getObjectType() {
		Class<?> aClass = getClass();

		if (aClass.isAnonymousClass()) {
			aClass = aClass.getSuperclass();
		}

		return aClass.getSimpleName();
	}
}
