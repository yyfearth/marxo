package marxo.dao;

import com.github.jmkgreen.morphia.dao.BasicDAO;
import marxo.Bean.Workflow;
import marxo.data.MongoDbConnector;
import org.bson.types.ObjectId;

public class WorkflowDao extends BasicDAO<Workflow, ObjectId> {
	static Class<Workflow> type = Workflow.class;

	protected WorkflowDao() {
		super(MongoDbConnector.getDatastore());
	}
}