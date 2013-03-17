package marxo.dao;

import com.github.jmkgreen.morphia.dao.BasicDAO;
import marxo.data.MongoDbConnector;
import org.bson.types.ObjectId;

/**
 * @param <E> Entity type
 */
public class BasicDao<E> extends BasicDAO<E, ObjectId> {
	public static BasicDao basicDao;

	public static BasicDao getInstance(Class<? extends BasicDao> clazz) throws IllegalAccessException, InstantiationException {
		if (basicDao == null) {
			basicDao = clazz.newInstance();
		}

		return basicDao;
	}

	protected BasicDao() {
		super(MongoDbConnector.getDatastore());
	}
}
