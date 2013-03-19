package marxo.dao;

import com.github.jmkgreen.morphia.dao.BasicDAO;
import marxo.data.MongoDbConnector;
import org.bson.types.ObjectId;

/**
 * @param <E> Entity type
 */
public class BasicDao<E> extends BasicDAO<E, ObjectId> {

	public BasicDao() {
		super(MongoDbConnector.getDatastore());
	}
}
