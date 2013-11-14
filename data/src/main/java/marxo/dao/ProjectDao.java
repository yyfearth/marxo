package marxo.dao;

import org.bson.types.ObjectId;

import java.util.List;

public class ProjectDao extends WorkflowDao {
	public ProjectDao(ObjectId tenantId) {
		super(tenantId);
		isProject = true;
	}

	@Override
	protected void processDataPairs(List<DataPair> dataPairs) {
		super.processDataPairs(dataPairs);
	}
}
