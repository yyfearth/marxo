package marxo.dao;

import com.github.jmkgreen.morphia.dao.BasicDAO;
import marxo.bean.Workflow;
import marxo.data.MongoDbConnector;
import org.bson.types.ObjectId;

public class WorkflowDao extends BasicDAO<Workflow, ObjectId> {
	static Class<Workflow> type = Workflow.class;

	public WorkflowDao() {
		super(MongoDbConnector.getDatastore());
	}
}