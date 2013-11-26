package marxo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import marxo.tool.Loggable;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Field;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Set;

// review: it's fucking weird that the entities are coupled with Jackson annotation.
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonPropertyOrder({"id", "email", "object_type", "tenant_id", "workflow_id", "name", "key", "desc", "context_type", "status", "content", "nodes", "node_ids", "links", "link_ids", "created_at", "created_by", "updated_at", "modified_by"})
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class BasicEntity implements Loggable {
	@JsonIgnore
	protected static final ApplicationContext applicationContext = new ClassPathXmlApplicationContext("mongo-configuration.xml");
	@JsonIgnore
	protected static final MongoTemplate mongoTemplate = applicationContext.getBean(MongoTemplate.class);
	@Id
	@JsonProperty("id")
	public ObjectId id = new ObjectId();
	@Field(order = 4)
	public String key = id.toString();
	@Field(order = 50)
	@JsonProperty("created_at")
	public DateTime createTime = DateTime.now();
	@Field(order = 52)
	@JsonProperty("updated_at")
	public DateTime updateTime = DateTime.now();
	@JsonProperty("created_by")
	@Field(order = 51)
	public ObjectId createUserId;
	@Field(order = 53)
	@JsonProperty("modified_by")
	public ObjectId updateUserId;
	@JsonProperty("desc")
	public String description = "";
	@Field(order = 3)
	protected String name = "";
	@Transient
	protected String objectType;

	@JsonIgnore
	protected static final Sort modifiedTimeSort = new Sort(Sort.Direction.ASC, "updateTime");

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
				"createUserId",
				"updateUserId"
		);
		for (String fieldName : strings) {
			java.lang.reflect.Field field = basicEntityClass.getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(newEntity, field.get(this));
		}
		return newEntity;
	}

	/*
	Validation
	 */

	protected boolean isValidated = false;

	public Boolean getValidated() {
		return isValidated;
	}

	public void wire() {

	}

	/**
	 * Also wire all references, which means database access. This means performance-critical.
	 */
	public void deepWire() {

	}

	public boolean validate() {
		throw new NotImplementedException();
	}

	/*
	DAO
	 */

	public void save() {
		// After a little test, it seems that insert also update the document with the same ID.
		mongoTemplate.save(this);
	}

	public void remove() {
		mongoTemplate.remove(this);
	}
}
