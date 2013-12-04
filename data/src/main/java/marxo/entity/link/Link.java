package marxo.entity.link;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import marxo.entity.node.Node;
import marxo.entity.workflow.WorkflowChildEntity;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Link extends WorkflowChildEntity {
	public Condition condition;

	@JsonProperty("prev_node_id")
	public ObjectId previousNodeId;
	@Transient
	@JsonIgnore
	protected Node previousNode;

	public Node getPreviousNode() {
		if (previousNodeId == null) {
			return null;
		}
		return (previousNode == null) ? (previousNode = Node.get(previousNodeId)) : previousNode;
	}

	@JsonProperty("next_node_id")
	public ObjectId nextNodeId;
	@Transient
	@JsonIgnore
	protected Node nextNode;

	public void setPreviousNode(Node previousNode) {
		this.previousNode = previousNode;
		this.previousNodeId = previousNode.id;
	}

	public Node getNextNode() {
		if (nextNodeId == null) {
			return null;
		}
		return (nextNode == null) ? (nextNode = Node.get(nextNodeId)) : nextNode;
	}

	public void setNextNode(Node nextNode) {
		this.nextNode = nextNode;
		this.nextNodeId = nextNode.id;
	}

	public boolean determine() {
		return true;
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
