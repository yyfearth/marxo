package marxo.dao;

import com.github.jmkgreen.morphia.dao.BasicDAO;
import marxo.bean.Node;
import marxo.data.MongoDbConnector;
import org.bson.types.ObjectId;

public class NodeDao extends BasicDAO<Node, ObjectId> {

	public NodeDao() {
		super(MongoDbConnector.getDatastore());
	}
}
