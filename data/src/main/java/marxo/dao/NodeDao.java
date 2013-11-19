package marxo.dao;

import marxo.entity.node.Node;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

@Repository
public class NodeDao extends WorkflowChildDao<Node> {
}
