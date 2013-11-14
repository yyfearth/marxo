package marxo.dao;

import marxo.entity.Link;
import org.bson.types.ObjectId;

public class LinkDao extends WorkflowChildDao<Link> {
	public LinkDao(ObjectId workflowId, ObjectId tenantId) {
		super(workflowId, tenantId);
	}

	public LinkDao(ObjectId tenantId) {
		super(tenantId);
	}
}
