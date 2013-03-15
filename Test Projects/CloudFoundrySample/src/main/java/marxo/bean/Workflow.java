package marxo.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.jmkgreen.morphia.annotations.Entity;
import marxo.tool.TypeTool;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity(value = "workflows", noClassnameStored = true)
public class Workflow extends BasicEntity {

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	String name;
	@JsonIgnore
	List<ObjectId> nodeIdList = null;
	@JsonIgnore
	List<ObjectId> linkIdList = null;
	@JsonIgnore
	ObjectId tenantId;
	WorkflowType type = null;
	WorkflowStatus status = null;

	@JsonProperty("nodeIdList")
	public Node[] getJsonNodes() {
		return TypeTool.toEntities(Node.class, nodeIdList);
	}

	@JsonProperty("nodeIdList")
	public void setJsonNodes(Node[] nodes) {
		nodeIdList = TypeTool.toIdList(nodes);
	}

	@JsonProperty("linkIdList")
	public Link[] getJsonLinks() {
		return TypeTool.toEntities(Link.class, linkIdList);
	}

	@JsonProperty("linkIdList")
	public void setJsonLinks(Link[] links) {
		linkIdList = TypeTool.toIdList(links);
	}

	@JsonProperty("tenantId")
	public String getJsonTenantId() {
		return (tenantId == null) ? null : tenantId.toString();
	}

	@JsonProperty("tenantId")
	public void setJsonTenantId(String tenantId) {
		this.tenantId = (tenantId == null) ? null : new ObjectId(tenantId);
	}

	public static Workflow getNew() {
		Workflow workflow = new Workflow();
		workflow.id = new ObjectId();
		workflow.createdDate = new Date();
		workflow.modifiedDate = new Date();

		workflow.type = WorkflowType.None;
		workflow.status = WorkflowStatus.None;

		workflow.nodeIdList = new ArrayList<ObjectId>();
		workflow.linkIdList = new ArrayList<ObjectId>();

		return workflow;
	}


}
