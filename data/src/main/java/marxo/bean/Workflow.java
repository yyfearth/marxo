package marxo.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class Workflow extends BasicEntity {
	public WorkflowType type = null;
	public WorkflowStatus status = null;
	@JsonIgnore
	public ObjectId tenantId;
	@JsonIgnore
	public List<ObjectId> nodeIdList = null;
	@JsonIgnore
	public List<ObjectId> linkIdList = null;

	public Workflow() {
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

	@JsonProperty("node_ids")
	public String[] getJsonNodeIds() {
		String[] ids = new String[nodeIdList.size()];
		for (int i = 0; i < nodeIdList.size(); i++) {
			ids[i] = nodeIdList.get(i).toString();
		}
		return ids;
	}

	@JsonProperty("link_ids")
	public String[] getJsonLinkIds() {
		String[] ids = new String[linkIdList.size()];
		for (int i = 0; i < linkIdList.size(); i++) {
			ids[i] = linkIdList.get(i).toString();
		}
		return ids;
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
