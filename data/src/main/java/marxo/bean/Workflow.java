package marxo.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import marxo.tool.TypeTool;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class Workflow extends BasicEntity {
	WorkflowType type = null;
	WorkflowStatus status = null;
	@JsonIgnore
	ObjectId tenantId;
	@JsonIgnore
	List<ObjectId> nodeIdList = null;
	@JsonIgnore
	List<ObjectId> linkIdList = null;

	public Workflow() {
	}

//	public String getDescription() {
//		return description;
//	}
//
//	public void setDescription(String description) {
//		this.description = description;
//	}

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

	public WorkflowStatus getStatus() {
		return status;
	}

	public void setStatus(WorkflowStatus status) {
		this.status = status;
	}

	@JsonProperty("nodes")
	public Node[] getJsonNodes() {
		return TypeTool.toEntities(Node.class, nodeIdList);
	}

	@JsonProperty("nodes")
	public void setJsonNodes(Node[] nodes) {
		nodeIdList = (nodes == null) ? new ArrayList<ObjectId>(0) : TypeTool.toIdList(nodes);
	}

	@JsonProperty("links")
	public Link[] getJsonLinks() {
		return TypeTool.toEntities(Link.class, linkIdList);
	}

	@JsonProperty("links")
	public void setJsonLinks(Link[] links) {
		linkIdList = (links == null) ? new ArrayList<ObjectId>() : TypeTool.toIdList(links);
	}

	@JsonProperty("tenant_id")
	public String getJsonTenantId() {
		return (tenantId == null) ? null : tenantId.toString();
	}

	@JsonProperty("tenant_id")
	public void setJsonTenantId(String tenantId) {
		this.tenantId = (tenantId == null) ? null : new ObjectId(tenantId);
	}

	public void fillWithDefaultValues() {
		super.fillWithDefaultValues();

		if (description == null) {
			description = "";
		}

		if (type == null) {
			type = WorkflowType.NONE;
		}

		if (status == null) {
			status = WorkflowStatus.NONE;
		}

		if (nodeIdList == null) {
			nodeIdList = new ArrayList<>();
		}

		if (linkIdList == null) {
			linkIdList = new ArrayList<>();
		}
	}
}
