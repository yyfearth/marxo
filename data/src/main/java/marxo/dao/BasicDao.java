package marxo.dao;

import marxo.bean.BasicEntity;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.lang.reflect.ParameterizedType;
import java.util.List;

public abstract class BasicDao<E extends BasicEntity> {

	@Autowired
	MongoTemplate mongoTemplate;
	Class<E> clazz;

	public BasicDao() {
		//noinspection unchecked
		Class<E> clazz = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		this.clazz = clazz;
	}

	public boolean exists(ObjectId id) {
		return mongoTemplate.findById(id, clazz) != null;
	}

	public void count(Query query) {
		mongoTemplate.count(query, clazz);
	}

	public void count() {
		mongoTemplate.count(new BasicQuery(""), clazz);
	}

	public void insert(E entity) {
		mongoTemplate.insert(entity);
	}

	public void insert(List<E> entities) {
		mongoTemplate.insert(entities, clazz);
	}

	public List<E> findAll() {
		return mongoTemplate.findAll(clazz);
	}

	public E get(ObjectId id) {
		return mongoTemplate.findById(id, clazz);
	}

	public List<E> find(Query query) {
		return mongoTemplate.find(query, clazz);
	}

	public void save(E entity) {
		mongoTemplate.save(entity);
	}

	public E delete(E entity) {
		return deleteById(entity.getId());
	}

	public E deleteById(ObjectId id) {
		return mongoTemplate.findAndRemove(Query.query(Criteria.where("id").is(id)), clazz);
	}
}
