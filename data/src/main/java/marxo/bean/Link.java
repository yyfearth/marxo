package marxo.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document
public class Link extends WorkflowChildEntity {

	@JsonIgnore
	public ObjectId tenantId;
	@JsonIgnore
	public ObjectId previousNodeId;
	@JsonIgnore
	public ObjectId nextNodeId;
	public Condition condition;

	@JsonProperty("prev_node_id")
	public String getJsonPreviousNodeId() {
		return previousNodeId == null ? null : previousNodeId.toString();
	}

	@JsonProperty("prev_node_id")
	public void setJsonPreviousNodeId(String previousNodeId) {
		this.previousNodeId = (previousNodeId == null) ? null : new ObjectId(previousNodeId);
	}

	@JsonProperty("next_node_id")
	public String getJsonNextNodeId() {
		return nextNodeId == null ? null : nextNodeId.toString();
	}

	@JsonProperty("next_node_id")
	public void setJsonNextNodeId(String next_node_id) {
		this.nextNodeId = (next_node_id == null) ? null : new ObjectId(next_node_id);
	}

	@Override
	public void fillWithDefaultValues() {
		super.fillWithDefaultValues();
	}
}
