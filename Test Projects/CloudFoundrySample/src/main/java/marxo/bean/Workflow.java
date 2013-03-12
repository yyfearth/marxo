package marxo.bean;

import com.github.jmkgreen.morphia.annotations.Entity;

import java.util.Arrays;
import java.util.UUID;

@Entity(value = "workflows", noClassnameStored = true)
public class Workflow extends BasicEntity {

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public UUID[] getNodeIds() {
		return nodeIds;
	}

	public void setNodeIds(UUID[] nodeIds) {
		this.nodeIds = nodeIds;
	}

	public UUID[] getLinkIds() {
		return linkIds;
	}

	public void setLinkIds(UUID[] linkIds) {
		this.linkIds = linkIds;
	}

	public UUID getTenantId() {
		return tenantId;
	}

	public void setTenantId(UUID tenantId) {
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
	UUID[] nodeIds;
	UUID[] linkIds;
	UUID tenantId;
	WorkflowType type = WorkflowType.None;
	WorkflowStatus status = WorkflowStatus.None;

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
