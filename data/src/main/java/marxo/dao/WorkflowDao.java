package marxo.dao;

import com.google.common.collect.Lists;
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

	public List<Workflow> findWithStatus(ProjectStatus status) {
		return find(Lists.newArrayList(
				new DataPair("status", status)
		));
	}

	public Workflow findOneWithStatus(ProjectStatus status) {
		return findOne(Lists.newArrayList(
				new DataPair("status", status)
		));
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
