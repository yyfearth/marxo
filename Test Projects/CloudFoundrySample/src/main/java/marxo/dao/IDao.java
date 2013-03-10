package marxo.dao;

import com.mongodb.BasicDBObject;

import java.util.UUID;

public interface IDao<T> {
	public boolean create(T entity);

	public T read(UUID id);

	public boolean update(T entity);

	public boolean createOrUpdate(T entity);

	public boolean delete(UUID id);

	public T findOne(UUID id);

	public T[] find(BasicDBObject query);

	boolean removeAll();
}
