package marxo.dao;

import com.google.common.base.Strings;
import com.mongodb.WriteResult;
import marxo.entity.BasicEntity;
import marxo.entity.Workflow;
import marxo.exception.DatabaseException;
import marxo.tool.StringTool;
import org.bson.types.ObjectId;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

@Repository
public abstract class BasicDao<E extends BasicEntity> implements IEntityDao<E> {
	static protected ApplicationContext context;
	static protected MongoTemplate mongoTemplate;

	static {
		context = new ClassPathXmlApplicationContext("classpath*:mongo-configuration.xml");
		mongoTemplate = (MongoTemplate) new ClassPathXmlApplicationContext("classpath*:mongo-configuration.xml").getBean("mongoTemplate");
	}

	protected Class<E> entityClass;

	public BasicDao() {
		this.entityClass = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}

	public Criteria getFilterCriteria() {
		return new Criteria();
	}

	@Override
	public boolean exists(ObjectId id) {
		return mongoTemplate.exists(Query.query(getFilterCriteria().and("id").is(id)), entityClass);
	}

	@Override
	public long count() {
		return mongoTemplate.count(Query.query(getFilterCriteria()), entityClass);
	}

	/*
	Create
	 */

	@Override
	public void insert(E entity) {
		mongoTemplate.insert(entity);
	}

	@Override
	public void insert(List<E> entities) {
		mongoTemplate.insert(entities, entityClass);
	}

	/*
	Read
	 */

	@Override
	public List<E> findAll() {
		return mongoTemplate.find(Query.query(getFilterCriteria()), entityClass);
	}

	@Override
	public E get(ObjectId id) {
		Criteria criteria = getFilterCriteria();
		Query query = Query.query(criteria.and("id").is(id));
		return mongoTemplate.findOne(query, entityClass);
	}

	/*
	Update
	 */

	@Override
	public void save(E entity) {
		mongoTemplate.save(entity);
	}

	public void updateField(String criteriaField, Object criteriaValue, String updateField, Object updateValue) {
		Criteria criteria = Criteria.where(criteriaField).is(criteriaValue);
		Update update = Update.update(updateField, updateValue);
		throwIfError(mongoTemplate.updateFirst(Query.query(criteria), update, entityClass));
	}

	/*
	Delete
	 */

	@Override
	public E deleteById(ObjectId id) {
		return mongoTemplate.findAndRemove(Query.query(getFilterCriteria().and("id").is(id)), entityClass);
	}

	/*
	Search
	 */

	public List<E> search(String field, Object value) {
		Criteria criteria = getFilterCriteria().andOperator(Criteria.where(field).is(value));
		return mongoTemplate.find(Query.query(criteria), entityClass);
	}

	public List<E> searchByWorkflowId(ObjectId workflowId) {
		return mongoTemplate.find(Query.query(getFilterCriteria().and("workflowId").is(workflowId)), entityClass);
	}

	/**
	 * Search the collection where the entity's name partially matched.
	 */
	@Override
	public List<Workflow> searchByName(String name) {
		if (Strings.isNullOrEmpty(name)) {
			return new ArrayList<>();
		}
		String escapedName = StringTool.escapePatternCharacters(name);
		return mongoTemplate.find(Query.query(getFilterCriteria().and("name").regex(".*" + escapedName + ".*")), Workflow.class);
	}

	/*
	Error handling
	 */

	public void throwIfError(WriteResult writeResult) {
		String error = writeResult.getError();
		if (!Strings.isNullOrEmpty(error)) {
			throw new DatabaseException(error);
		}
	}
}
