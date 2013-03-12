package marxo.dao;

import com.github.jmkgreen.morphia.Datastore;
import com.github.jmkgreen.morphia.Key;
import com.github.jmkgreen.morphia.query.Query;
import com.mongodb.WriteResult;
import marxo.Bean.BasicEntity;
import marxo.Bean.Workflow;
import marxo.data.MongoDbConnector;

import java.lang.reflect.Array;
import java.util.List;
import java.util.UUID;

public class BasicDao<T extends BasicEntity> implements IDao<T> {
	Datastore datastore;

	public BasicDao() {
		datastore = MongoDbConnector.getDatastore();
	}

	@Override
	public boolean create(T entity) {
		Key<T> key = datastore.save(entity);

		return key != null;
	}

	@Override
	public T read(Class<T> type, UUID id) {
		return datastore.get(type, id);
	}

	@Override
	public boolean update(T entity) {
		Query<Workflow> query = datastore.createQuery(Workflow.class).field("id").equal(entity.getId());

		return datastore.findAndDelete(query) != null && create(entity);

	}

	@Override
	public boolean delete(Class<T> type, UUID id) {
		WriteResult writeResult = datastore.delete(type, id);
		return writeResult.getError() == null;
	}

	@Override
	public T findOne(Class<T> type, String property, Object value) {
		Query<T> query = datastore.find(type, property, value);
		return query.get();
	}

	@Override
	public T[] find(Class<T> type, String property, Object value) {
		Query<T> query = datastore.find(type, property, value);
		List<T> list = query.asList();
		T[] entities = (T[]) Array.newInstance(type, list.size());
		list.toArray(entities);
		return entities;
	}

	@Override
	public boolean removeAll(Class<T> type) {
		WriteResult writeResult = datastore.delete(datastore.createQuery(type));
		return writeResult.getError() == null;
	}
}
