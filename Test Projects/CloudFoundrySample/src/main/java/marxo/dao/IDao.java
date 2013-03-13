package marxo.dao;

import java.util.UUID;

public interface IDao<T> {
	boolean create(T entity);

	T read(Class<T> type, UUID id);

	boolean update(T entity);

	boolean delete(Class<T> type, UUID id);

	T findOne(Class<T> type, String property, Object value);

	T[] find(Class<T> type, String property, Object value);

	boolean removeAll(Class<T> type);
}
