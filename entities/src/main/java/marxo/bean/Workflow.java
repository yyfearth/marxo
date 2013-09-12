package marxo.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import marxo.tool.TypeTool;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@JsonPropertyOrder({"id", "tenantId", "name", "title", "desc", "type", "status", "nodes", "links", "created", "createdBy", "modified", "modifiedBy", "objectType"})
public class Workflow extends BasicEntity {

	@JsonProperty("desc")
	String description;
	WorkflowType type = null;
	WorkflowStatus status = null;
	@JsonIgnore
	ObjectId tenantId;
	@JsonIgnore
	List<ObjectId> nodeIdList = null;
	@JsonIgnore
	List<ObjectId> linkIdList = null;

	public Workflow() {
		id = new ObjectId();
		createdDate = new Date();
		modifiedDate = new Date();

		type = WorkflowType.NONE;
		status = WorkflowStatus.NONE;

		nodeIdList = new ArrayList<>();
		linkIdList = new ArrayList<>();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

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

	public ObjectId getTenantId() {
		return tenantId;
	}

	public void setTenantId(ObjectId tenantId) {
		this.tenantId = tenantId;
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

	@JsonProperty("tenantId")
	public String getJsonTenantId() {
		return (tenantId == null) ? null : tenantId.toString();
	}

	@JsonProperty("tenantId")
	public void setJsonTenantId(String tenantId) {
		this.tenantId = (tenantId == null) ? null : new ObjectId(tenantId);
	}
}
