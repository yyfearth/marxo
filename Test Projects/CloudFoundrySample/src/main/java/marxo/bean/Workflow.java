package marxo.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.jmkgreen.morphia.annotations.Entity;
import marxo.tool.TypeTool;
import org.bson.types.ObjectId;

import java.util.Arrays;

@Entity(value = "workflows", noClassnameStored = true)
public class Workflow extends BasicEntity {

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ObjectId[] getNodeIds() {
		return nodeIds;
	}

	public void setNodeIds(ObjectId[] nodeIds) {
		this.nodeIds = nodeIds;
	}

	public ObjectId[] getLinkIds() {
		return linkIds;
	}

	public void setLinkIds(ObjectId[] linkIds) {
		this.linkIds = linkIds;
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
	ObjectId[] nodeIds = new ObjectId[0];
	@JsonIgnore
	ObjectId[] linkIds = new ObjectId[0];
	@JsonIgnore
	ObjectId tenantId;
	WorkflowType type = WorkflowType.None;
	WorkflowStatus status = WorkflowStatus.None;

	public String[] getJsonNodeIds() {
		return TypeTool.objectIdsToStrings(nodeIds);
	}

	public void setJsonNodeIds(String[] nodeIds) {
		this.nodeIds = TypeTool.stringsToObjectIds(nodeIds);
	}

	@JsonProperty("linkIds")
	public String[] getJsonLinkIds() {
		return TypeTool.objectIdsToStrings(linkIds);
	}

	public void setJsonLinkIds(String[] linkIds) {
		this.linkIds = TypeTool.stringsToObjectIds(linkIds);
	}

	public String getJsonTenantId() {
		return (tenantId == null) ? null : tenantId.toString();
	}

	public void setJsonTenantId(String tenantId) {
		this.tenantId = (tenantId == null) ? null : new ObjectId(tenantId);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Workflow)) return false;

		Workflow workflow = (Workflow) o;

		if (!Arrays.equals(linkIds, workflow.linkIds)) return false;
		if (name != null ? !name.equals(workflow.name) : workflow.name != null) return false;
		if (!Arrays.equals(nodeIds, workflow.nodeIds)) return false;
		if (status != workflow.status) return false;
		if (tenantId != null ? !tenantId.equals(workflow.tenantId) : workflow.tenantId != null) return false;
		if (type != workflow.type) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = name != null ? name.hashCode() : 0;
		result = 31 * result + (nodeIds != null ? Arrays.hashCode(nodeIds) : 0);
		result = 31 * result + (linkIds != null ? Arrays.hashCode(linkIds) : 0);
		result = 31 * result + (tenantId != null ? tenantId.hashCode() : 0);
		result = 31 * result + (type != null ? type.hashCode() : 0);
		result = 31 * result + (status != null ? status.hashCode() : 0);
		return result;
	}
}
