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
		//noinspection unchecked
		this.eClass = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}

	public boolean exists(ObjectId id) {
		return mongoTemplate.findById(id, eClass) != null;
	}

	public void count(Query query) {
		mongoTemplate.count(query, eClass);
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

	public List<E> find(Query query) {
		return mongoTemplate.find(query, eClass);
	}

	// Update
	public void save(E entity) throws ValidationException {
		mongoTemplate.save(entity);
	}

	// Delete
	public E delete(E entity) {
		return deleteById(entity.getId());
	}

	public E deleteById(ObjectId id) {
		return mongoTemplate.findAndRemove(Query.query(Criteria.where("id").is(id)), eClass);
	}
}
