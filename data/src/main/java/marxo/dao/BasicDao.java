package marxo.dao;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mongodb.WriteResult;
import marxo.entity.BasicEntity;
import marxo.exception.DatabaseException;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.lang.reflect.ParameterizedType;
import java.util.List;

@SuppressWarnings("ALL")
public abstract class BasicDao<Entity extends BasicEntity> {
	static protected ApplicationContext context;
	static protected MongoTemplate mongoTemplate;

	static {
		context = new ClassPathXmlApplicationContext("classpath*:mongo-configuration.xml");
		mongoTemplate = (MongoTemplate) new ClassPathXmlApplicationContext("classpath*:mongo-configuration.xml").getBean("mongoTemplate");
	}

	protected final Class<Entity> entityClass;

	public BasicDao() {
		this.entityClass = (Class<Entity>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}

	/*
	Create
	 */

	public void insert(List<Entity> entities) {
		for (Entity entity : entities) {
			entity.createdDate = entity.modifiedDate = DateTime.now();
		}

		mongoTemplate.insert(entities, entityClass);
	}

	public void insert(Entity entity) {
		insert(Lists.newArrayList(entity));
	}

	/*
	Read
	 */

	public Entity findOne(DaoContext context) {
		return mongoTemplate.findOne(context.toQuery(), entityClass);
	}

	public Entity findOne(ObjectId id, DaoContext context) {
		return findOne(context.addContext("id", id));
	}

	public Entity findOne(ObjectId id) {
		return mongoTemplate.findById(id, entityClass);
	}

	public Entity findAndRemove(DaoContext context) {
		return mongoTemplate.findAndRemove(context.toQuery(), entityClass);
	}

	public List<Entity> find(DaoContext context) {
		return mongoTemplate.find(context.toQuery(), entityClass);
	}

	public List<Entity> find() {
		return find(DaoContext.newInstance());
	}

	/**
	 * Search the collection where the entity's name partially matched.
	 */
	public List<Entity> findByName(String name, DaoContext context) {
		return find(context.addContext("name", DaoContextOperator.LIKE, name));
	}

	/*
	Update
	 */

	public void save(Entity entity) {
		mongoTemplate.save(entity);
	}

	/**
	 * @return The number of documents got updated.
	 */
	public int updateFirst(DaoContext context, DaoContext updateContext) {
		WriteResult writeResult = mongoTemplate.updateFirst(context.toQuery(), updateContext.toUpdate(), entityClass);
		throwIfError(writeResult);
		return writeResult.getN();
	}

	public int update(DaoContext context, DaoContext updateContext) {
		WriteResult writeResult = mongoTemplate.updateMulti(context.toQuery(), updateContext.toUpdate(), entityClass);
		throwIfError(writeResult);
		return writeResult.getN();
	}

	/*
	Remove
	 */

	public Entity findAndRemove() {
		return findAndRemove(DaoContext.newInstance());
	}

	public void remove(Entity entity) {
		mongoTemplate.remove(entity);
	}

	public void remove(DaoContext context) {
		mongoTemplate.remove(context.toQuery(), entityClass);
	}

	/*
	Utilities
	 */

	public boolean exists(DaoContext context) {
		return mongoTemplate.exists(context.toQuery(), entityClass);
	}

	public boolean exists(ObjectId id, DaoContext context) {
		return exists(context.addContext("id", id));
	}

	public long count(DaoContext context) {
		return mongoTemplate.count(context.toQuery(), entityClass);
	}

	public long count() {
		return count(DaoContext.newInstance());
	}

	protected void throwIfError(WriteResult writeResult) {
		String error = writeResult.getError();
		if (!Strings.isNullOrEmpty(error)) {
			throw new DatabaseException(error);
		}
	}
}
