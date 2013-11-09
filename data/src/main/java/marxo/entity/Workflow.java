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
	@Transient
	public List<Node> nodes;
	@Transient
	public List<Link> links;
	public WorkflowStatus status;

	public List<ObjectId> getLinkIdList() {
		return linkIdList;
	}

	public void setLinkIdList(List<ObjectId> linkIdList) {
		this.linkIdList = linkIdList;
	}

	public List<ObjectId> getNodeIdList() {
		return nodeIdList;
	}

	public void setNodeIdList(List<ObjectId> nodeIdList) {
		this.nodeIdList = nodeIdList;
	}

	public WorkflowType getType() {
		return type;
	}

	public void setType(WorkflowType type) {
		this.type = type;
	}

	@JsonProperty("node_ids")
	public String[] getJsonNodeIds() {
		if (nodeIdList == null) {
			return new String[0];
		}

		String[] ids = new String[nodeIdList.size()];
		for (int i = 0; i < nodeIdList.size(); i++) {
			ids[i] = nodeIdList.get(i).toString();
		}
		return ids;
	}

	@JsonProperty("link_ids")
	public String[] getJsonLinkIds() {
		if (linkIdList == null) {
			return new String[0];
		}
		String[] ids = new String[linkIdList.size()];
		for (int i = 0; i < linkIdList.size(); i++) {
			ids[i] = linkIdList.get(i).toString();
		}
		return ids;
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
