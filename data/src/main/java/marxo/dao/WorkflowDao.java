package marxo.dao;

import com.google.common.collect.Lists;
import com.mongodb.WriteResult;
import marxo.entity.workflow.ProjectStatus;
import marxo.entity.workflow.Workflow;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class WorkflowDao extends TenantChildDao<Workflow> {
}

class MapNode {
	ObjectId nodeId;
	ObjectId fromlinkId;
	List<MapNode> list = new ArrayList<>();

	MapNode(ObjectId nodeId, ObjectId fromlinkId) {
		this.nodeId = nodeId;
		this.fromlinkId = fromlinkId;
	}
}