package marxo.dao;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mongodb.WriteResult;
import marxo.entity.BasicEntity;
import marxo.exception.DatabaseException;
import marxo.tool.StringTool;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@SuppressWarnings("ALL")
public abstract class BasicDao<Entity extends BasicEntity> {
	static protected ApplicationContext context;
	static protected MongoTemplate mongoTemplate;

	static {
		context = new ClassPathXmlApplicationContext("classpath*:mongo-configuration.xml");
		mongoTemplate = (MongoTemplate) new ClassPathXmlApplicationContext("classpath*:mongo-configuration.xml").getBean("mongoTemplate");
	}

	protected Class<Entity> entityClass;

	public BasicDao() {
		this.entityClass = (Class<Entity>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}

	/**
	 * Chain all filter with And Operator and create a Update object.
	 * Return empty update if daoFilters is null.
	 */
	protected Update dataPairsToUpdate(List<DataPair> dataPairs) {
		Update update = new Update();
		if (dataPairs != null) {
			for (DataPair dataPair : dataPairs) {
				update = update.set(dataPair.field, dataPair.value);
			}
		}
		return update;
	}

	/*
	Create
	 */

	public void insert(List<Entity> entities) {
		for (Entity entity : entities) {
			entity.fillWithDefaultValues();
			entity.createdDate = entity.modifiedDate = DateTime.now();
		}

		processEntities(entities);

		mongoTemplate.insert(entities, entityClass);
	}

	public void insert(Entity entity) {
		insert(Lists.newArrayList(entity));
	}

	/*
	Read
	 */

	public Entity findOne(List<DataPair> dataPairs) {
		processDataPairs(dataPairs);
		Query query = Query.query(dataPairsToCriteria(dataPairs));
		return mongoTemplate.findOne(query, entityClass);
	}

	public Entity findOne(ObjectId id) {
		return findOne(new ArrayList<>(Arrays.asList(
				new DataPair("id", id)
		)));
	}

	public Entity findAndRemove(List<DataPair> dataPairs) {
		Query query = Query.query(dataPairsToCriteria(dataPairs));
		return mongoTemplate.findAndRemove(query, entityClass);
	}

	public List<Entity> find(List<DataPair> dataPairs) {
		processDataPairs(dataPairs);
		Query query = Query.query(dataPairsToCriteria(dataPairs));
		return mongoTemplate.find(query, entityClass);
	}

	public List<Entity> find(String field, Object value) {
		return find(new ArrayList<>(Arrays.asList(
				new DataPair(field, value)
		)));
	}

	public List<Entity> find() {
		return find(new ArrayList<DataPair>());
	}

	/**
	 * Search the collection where the entity's name partially matched.
	 */
	public List<Entity> findByName(String name) {
		return find(new ArrayList<>(Arrays.asList(
				new DataPair("name", DataPairOperator.LIKE, name)
		)));
	}

	/*
	Update
	 */

	public void save(Entity entity) {
		entity.modifiedDate = DateTime.now();

		processEntities(new ArrayList<>(Arrays.asList(entity)));

		mongoTemplate.save(entity);
	}

	public void update(List<DataPair> criteriaList, List<DataPair> updateList) {
		processDataPairs(criteriaList);
		throwIfError(mongoTemplate.updateFirst(Query.query(dataPairsToCriteria(criteriaList)), dataPairsToUpdate(updateList), entityClass));
	}

	/*
	Delete
	 */

	public Entity delete(List<DataPair> dataPairs) {
		processDataPairs(dataPairs);
		return mongoTemplate.findAndRemove(Query.query(dataPairsToCriteria(dataPairs)), entityClass);
	}

	public Entity deleteById(ObjectId id) {
		return delete(new ArrayList<>(Arrays.asList(
				new DataPair("id", id)
		)));
	}

	public void remove(List<DataPair> dataPairs) {
		processDataPairs(dataPairs);
		mongoTemplate.remove(Query.query(dataPairsToCriteria(dataPairs)), entityClass);
	}

	/*
	Utilities
	 */

	public boolean exists(List<DataPair> dataPairs) {
		processDataPairs(dataPairs);
		Query query = Query.query(dataPairsToCriteria(dataPairs));
		return mongoTemplate.exists(query, entityClass);
	}

	public boolean exists(ObjectId id) {
		return exists(new ArrayList<>(Arrays.asList(
				new DataPair("id", id)
		)));
	}

	public long count(List<DataPair> dataPairs) {
		processDataPairs(dataPairs);
		Query query = Query.query(dataPairsToCriteria(dataPairs));
		return mongoTemplate.count(query, entityClass);
	}

	public long count() {
		return count(new ArrayList<DataPair>());
	}

	/**
	 * Chain all filter with And Operator and create a Criteria object.
	 * Return empty criteria if daoFilters is null.
	 */
	protected Criteria dataPairsToCriteria(List<DataPair> dataPairs) {
		Criteria criteria = new Criteria();
		for (DataPair dataPair : dataPairs) {
			switch (dataPair.operator) {
				case IN:
					criteria = criteria.and(dataPair.field).in(dataPair.value);
					break;
				case LIKE:
					String escapedName = StringTool.escapePatternCharacters(dataPair.value.toString());
					Pattern pattern = Pattern.compile(".*" + escapedName + ".*", Pattern.CASE_INSENSITIVE);
					criteria = criteria.and(dataPair.field).regex(pattern);
					break;
				case IS:
					criteria = criteria.and(dataPair.field).is(dataPair.value);
					break;
			}
		}
		return criteria;
	}

	protected void throwIfError(WriteResult writeResult) {
		String error = writeResult.getError();
		if (!Strings.isNullOrEmpty(error)) {
			throw new DatabaseException(error);
		}
	}

	/**
	 * The method will be called before construct the real database query.
	 *
	 * @param entity The entity to be used for database query.
	 */
	protected void processEntity(Entity entity) {
	}

	private void processEntities(List<Entity> entities) {
		for (Entity entity : entities) {
			processEntity(entity);
		}
	}

	/**
	 * The method will be called before construct the real database query.
	 */
	protected void processDataPairs(List<DataPair> dataPairs) {
	}
}
