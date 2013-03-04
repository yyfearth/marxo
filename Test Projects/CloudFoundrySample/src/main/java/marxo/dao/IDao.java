package marxo.dao;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import java.util.UUID;

public interface IDao<T extends DBObject> {
	public boolean create(T obj);

	public T read(UUID id);

	public boolean update(T obj);

	public boolean createOrUpdate(T obj);

	public boolean delete(UUID id);

	public T findOne(UUID id);

	public T[] find(BasicDBObject query);
}
