package marxo.entity.node;

import com.fasterxml.jackson.annotation.JsonIgnore;
import marxo.entity.workflow.WorkflowChildEntity;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Transient;

public abstract class NodeChildEntity extends WorkflowChildEntity {

	public ObjectId nodeId;
	@Transient
	protected Node node;

	@JsonIgnore
	public Node getNode() {
		if (nodeId == null) {
			return null;
		}
		return (node == null) ? (node = mongoTemplate.findById(this.nodeId, Node.class)) : node;
	}

	@JsonIgnore
	public void setNode(Node node) {
		this.node = node;
		this.nodeId = node.id;
		this.workflowId = node.workflowId;
		this.tenantId = node.tenantId;
	}
}
