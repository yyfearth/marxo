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
	/**
	 * The pre-defined filter which will constrain the results.
	 */
	protected Criteria filterCriteria = new Criteria();
	// review: find a way to autowire this. Currently, when a DAO is loaded, it has no info about mongo-configuration.xml. Changing context order doesn't help.
	MongoTemplate mongoTemplate;
	ApplicationContext context;

	public BasicDao() {
		context = new ClassPathXmlApplicationContext("classpath*:mongo-configuration.xml");
		mongoTemplate = (MongoTemplate) new ClassPathXmlApplicationContext("classpath*:mongo-configuration.xml").getBean("mongoTemplate");
		this.entityClass = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}

	@Override
	public boolean exists(ObjectId id) {
		return mongoTemplate.exists(Query.query(filterCriteria.and("id").is(id)), entityClass);
	}

	@Override
	public long count() {
		return mongoTemplate.count(Query.query(filterCriteria), entityClass);
	}

	// Create
	@Override
	public void insert(E entity) {
		mongoTemplate.insert(entity);
	}

	@Override
	public void insert(List<E> entities) {
		mongoTemplate.insert(entities, entityClass);
	}

	// Read
	@Override
	public List<E> findAll() {
		return mongoTemplate.find(Query.query(filterCriteria), entityClass);
	}

	@Override
	public E get(ObjectId id) {
		return mongoTemplate.findOne(Query.query(filterCriteria.and("id").is(id)), entityClass);
	}

	// Update
	@Override
	public void save(E entity) {
		mongoTemplate.save(entity);
	}

	// Delete
	@Override
	public E deleteById(ObjectId id) {
		return mongoTemplate.findAndRemove(Query.query(filterCriteria.and("id").is(id)), entityClass);
	}

	// Search
	public List<E> searchByWorkflowId(ObjectId workflowId) {
		return mongoTemplate.find(Query.query(filterCriteria.and("workflowId").is(workflowId)), entityClass);
	}

	public List<E> searchByWorkflowIds(List<ObjectId> workflowIds) {
		filterCriteria.
		return mongoTemplate.find(Query.query(filterCriteria.and("workflowId").in(workflowIds)), entityClass);
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
		return mongoTemplate.find(Query.query(filterCriteria.and("name").regex(".*" + escapedName + ".*")), Workflow.class);
	}
}
