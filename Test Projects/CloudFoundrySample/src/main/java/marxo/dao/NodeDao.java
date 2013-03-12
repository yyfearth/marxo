package marxo.dao;

import com.github.jmkgreen.morphia.dao.BasicDAO;
import marxo.bean.SharedNode;
import marxo.data.MongoDbConnector;

import java.util.UUID;

public class NodeDao extends BasicDAO<SharedNode, UUID> {

	protected NodeDao() {
		super(MongoDbConnector.getDatastore());
	}
}
