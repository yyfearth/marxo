package marxo.dao;

import marxo.entity.BasicEntity;
import marxo.entity.Workflow;
import org.bson.types.ObjectId;

import java.util.List;

public interface IEntityDao<E extends BasicEntity> {
	boolean exists(ObjectId id);

	long count();

	// Create
	void insert(E entity);

	void insert(List<E> entities);

	// Read
	List<E> findAll();

	E get(ObjectId id);

	// Update
	void save(E entity);

	// Delete
	E deleteById(ObjectId id);

	List<Workflow> searchByName(String name);
}
