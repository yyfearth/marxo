package marxo.dao;

import marxo.bean.Entity;
import marxo.exception.ValidationException;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.lang.reflect.ParameterizedType;
import java.util.List;

public abstract class BasicDao<E extends Entity> {

	@Qualifier("mongoTemplate")
	@Autowired
	MongoTemplate mongoTemplate;
	Class<E> eClass;

	public BasicDao() {
		this.eClass = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}

	public boolean exists(ObjectId id) {
		return mongoTemplate.findById(id, eClass) != null;
	}

	public void count() {
		mongoTemplate.count(new BasicQuery(""), eClass);
	}

	// Create
	public void insert(E entity) throws ValidationException {
		mongoTemplate.insert(entity);
	}

	public void insert(List<E> entities) throws ValidationException {
		mongoTemplate.insert(entities, eClass);
	}

	// Read
	public List<E> findAll() {
		return mongoTemplate.findAll(eClass);
	}

	public E get(ObjectId id) {
		return mongoTemplate.findById(id, eClass);
	}

	// Update
	public void save(E entity) throws ValidationException {
		mongoTemplate.save(entity);
	}

	// Delete
	public E delete(E entity) {
		return deleteById(entity.id);
	}

	public E deleteById(ObjectId id) {
		return mongoTemplate.findAndRemove(Query.query(Criteria.where("id").is(id)), eClass);
	}

	// Search
	public List<E> searchByWorkflow(ObjectId workflowId) {
		List<E> entities = mongoTemplate.find(Query.query(Criteria.where("workflowId").is(workflowId)), eClass);
		return entities;
	}

	public List<E> searchByWorkflows(List<ObjectId> workflowIds) {
		List<E> entities = mongoTemplate.find(Query.query(Criteria.where("workflowId").in(workflowIds)), eClass);
		return entities;
	}
}
