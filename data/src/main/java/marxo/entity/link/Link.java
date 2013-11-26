package marxo.entity.link;

import com.fasterxml.jackson.annotation.JsonProperty;
import marxo.entity.node.Node;
import marxo.entity.workflow.WorkflowChildEntity;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Link extends WorkflowChildEntity {
	@JsonProperty("prev_node_id")
	public ObjectId previousNodeId;
	@JsonProperty("next_node_id")
	public ObjectId nextNodeId;
	public Condition condition;
	@Transient
	protected Node previousNode;
	@Transient
	protected Node nextNode;

	public Node getPreviousNode() {
		return previousNode;
	}

	public void setPreviousNode(Node previousNode) {
		this.previousNode = previousNode;
		this.previousNodeId = previousNode.id;
	}

	public Node getNextNode() {
		return nextNode;
	}

	public void setNextNode(Node nextNode) {
		this.nextNode = nextNode;
		this.nextNodeId = nextNode.id;
	}

	/*
	DAO
	 */

	public static Link get(ObjectId id) {
		Link link = mongoTemplate.findById(id, Link.class);
		if (link != null) {
			link.wire();
		}
		return link;
	}
}
