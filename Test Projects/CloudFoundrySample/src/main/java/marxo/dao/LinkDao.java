package marxo.dao;

import com.github.jmkgreen.morphia.dao.BasicDAO;
import marxo.bean.Link;
import marxo.data.MongoDbConnector;
import org.bson.types.ObjectId;

public class LinkDao extends BasicDAO<Link, ObjectId> {

	public LinkDao() {
		super(MongoDbConnector.getDatastore());
	}
}
