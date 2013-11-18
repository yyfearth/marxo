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
	public Node previousNode;
	@Transient
	public Node nextNode;
}
