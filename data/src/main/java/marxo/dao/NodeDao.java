package marxo.dao;

import marxo.entity.Node;
import org.bson.types.ObjectId;

public class NodeDao extends WorkflowChildDao<Node> {
	public NodeDao(ObjectId workflowId, ObjectId tenantId) {
		super(workflowId, tenantId);
	}

	public NodeDao(ObjectId tenantId) {
		super(tenantId);
	}
}
