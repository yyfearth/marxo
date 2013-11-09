package marxo.dao;

import com.google.common.base.Strings;
import marxo.entity.BasicEntity;
import marxo.entity.Workflow;
import marxo.tool.StringTool;
import org.bson.types.ObjectId;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

@Repository
public abstract class BasicDao<E extends BasicEntity> implements IEntityDao<E> {
	protected Class<E> entityClass;
	MongoTemplate mongoTemplate;
	ApplicationContext context;

	public BasicDao() {
		context = new ClassPathXmlApplicationContext("classpath*:mongo-configuration.xml");
		mongoTemplate = (MongoTemplate) new ClassPathXmlApplicationContext("classpath*:mongo-configuration.xml").getBean("mongoTemplate");
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

	public List<E> searchByWorkflowIds(List<ObjectId> workflowIds) {
		return mongoTemplate.find(Query.query(getFilterCriteria().and("workflowId").in(workflowIds)), entityClass);
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
}
