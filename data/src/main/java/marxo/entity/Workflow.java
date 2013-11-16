package marxo.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Transient;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(value = {
		"nodeIds", "linkIds", "nodes", "links", "startNodeId", "currentNodeId", "endNodeId"
}, ignoreUnknown = true)
public class Workflow extends TenantChildEntity {
	public WorkflowType type = WorkflowType.NONE;
	public List<ObjectId> nodeIds;
	public List<ObjectId> linkIds;
	@Transient
	public List<Node> nodes;
	@Transient
	public List<Link> links;
	public ProjectStatus status = ProjectStatus.IDLE;
	@JsonProperty("is_project")
	public boolean isProject = false;
	/*
	Internal engine data
	 */
	public ObjectId startNodeId;
	public ObjectId currentNodeId;
	public ObjectId endNodeId;
	public ObjectId currentActionId;

	@JsonProperty("link_ids")
	public List<ObjectId> getLinkIds() {
		return (linkIds == null) ? new ArrayList<ObjectId>() : linkIds;
	}

	@JsonProperty("node_ids")
	public List<ObjectId> getNodeIds() {
		return (nodeIds == null) ? new ArrayList<ObjectId>() : nodeIds;
	}

	@JsonProperty("nodes")
	public List<Node> getNodes() {
		return (nodes == null) ? new ArrayList<Node>() : nodes;
	}

	@JsonProperty("links")
	public List<Link> getLinks() {
		return (links == null) ? new ArrayList<Link>() : links;
	}

	public void fillWithDefaultValues() {
		super.fillWithDefaultValues();

		if (description == null) {
			description = "";
		}

		if (nodeIds == null) {
			nodeIds = new ArrayList<>();
		}

		if (linkIds == null) {
			linkIds = new ArrayList<>();
		}

		if (nodes == null) {
			nodes = new ArrayList<>();
		}

		if (links == null) {
			links = new ArrayList<>();
		}
	}
}
