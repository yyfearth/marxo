package marxo.entity;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Transient;

import java.util.ArrayList;
import java.util.List;

public class Workflow extends TenantChildEntity {
	public WorkflowType type = null;
	public List<ObjectId> nodeIdList = null;
	public List<ObjectId> linkIdList = null;
	@Transient
	public List<Node> nodes;
	@Transient
	public List<Link> links;
	public WorkflowStatus status;

	public WorkflowType getType() {
		return type;
	}

	public void setType(WorkflowType type) {
		this.type = type;
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
