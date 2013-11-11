package marxo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Transient;

import java.util.ArrayList;
import java.util.List;

public class Workflow extends TenantChildEntity {
	public WorkflowType type = null;
	@JsonIgnore
	public List<ObjectId> nodeIdList = null;
	@JsonIgnore
	public List<ObjectId> linkIdList = null;
	@JsonIgnore
	@Transient
	public List<Node> nodes;
	@JsonIgnore
	@Transient
	public List<Link> links;
	public WorkflowStatus status;

	@JsonProperty("nodes")
	public List<Node> getNodes() {
		return nodes;
	}

	@JsonProperty("links")
	public List<Link> getLinks() {
		return links;
	}

	public void fillWithDefaultValues() {
		super.fillWithDefaultValues();

		if (description == null) {
			description = "";
		}

		if (type == null) {
			type = WorkflowType.NONE;
		}

		if (nodeIdList == null) {
			nodeIdList = new ArrayList<>();
		}

		if (linkIdList == null) {
			linkIdList = new ArrayList<>();
		}

		if (nodes == null) {
			nodes = new ArrayList<>();
		}

		if (links == null) {
			links = new ArrayList<>();
		}

		if (status == null) {
			status = WorkflowStatus.IDLE;
		}
	}
}
