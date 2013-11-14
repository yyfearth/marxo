package marxo.dao;

import com.mongodb.WriteResult;
import marxo.entity.ProjectStatus;
import marxo.entity.Workflow;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

public class WorkflowDao extends TenantChildDao<Workflow> {
	boolean isProject = false;

	public WorkflowDao(ObjectId tenantId) {
		super(tenantId);
	}

	public WorkflowDao(ObjectId tenantId, boolean isProject) {
		super(tenantId);
		this.isProject = isProject;
	}

	/**
	 * Get next project which is waiting to be processed.
	 */
	public Workflow getNextProject() {
//		mongoTemplate.findAndModify()
		Criteria criteria = Criteria.where("isProject").is(true).and("status").is(ProjectStatus.IDLE);
		return mongoTemplate.findOne(org.springframework.data.mongodb.core.query.Query.query(criteria), entityClass);
	}

	public void setStatus(ObjectId workflowId, ProjectStatus status) {
		Criteria criteria = Criteria.where("workflowId").is(workflowId);
		Update update = Update.update("status", status);
		WriteResult writeResult = mongoTemplate.updateFirst(Query.query(criteria), update, entityClass);
		throwIfError(writeResult);
	}

	@Override
	protected void processEntity(Workflow entity) {
		entity.isProject = isProject;
		super.processEntity(entity);
	}

	@Override
	protected void processDataPairs(List<DataPair> dataPairs) {
		dataPairs.add(new DataPair("isProject", isProject));
		super.processDataPairs(dataPairs);
	}
}
