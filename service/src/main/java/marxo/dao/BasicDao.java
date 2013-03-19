package marxo.dao;

import com.github.jmkgreen.morphia.dao.BasicDAO;
import com.github.jmkgreen.morphia.query.Query;
import com.github.jmkgreen.morphia.query.QueryResults;
import marxo.data.MongoDbConnector;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @param <E> Entity type
 */
public class BasicDao<E> extends BasicDAO<E, ObjectId> {

	public BasicDao() {
		super(MongoDbConnector.getDatastore());
	}

	public boolean exists(ObjectId id) {
		return exists("_id", id);
	}

	public boolean exists(String id) {
		return exists(new ObjectId(id));
	}

	public List<E> findAll() {
		QueryResults<E> entities = super.find();
		return entities.asList();
	}

	public List<E> findBy(Query<E> query) {
		QueryResults<E> entities = super.find(query);
		return entities.asList();
	}

	public List<E> findBy(String condition, Object value) {
		return findBy(createQuery().filter(condition, value));
	}

	public List<E> findBy(String field, ObjectId id) {
		return findBy(createQuery().field(field).equal(id));
	}

	public List<E> findBy(String field, String id) {
		return findBy(createQuery().field(field).equal(new ObjectId(id)));
	}

	public List<E> findBy(String field, Collection<ObjectId> ids) {
		return findBy(createQuery().field(field).in(ids));
	}

	public List<E> findBy(String field, String[] ids) {
		List<ObjectId> idList = new ArrayList<ObjectId>(ids.length);
		for (String id : ids) {
			idList.add(new ObjectId(id));
		}
		return findBy(createQuery().field(field).in(idList));
	}
}
