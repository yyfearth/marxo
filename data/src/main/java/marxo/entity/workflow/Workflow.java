package marxo.entity.workflow;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import marxo.entity.link.Link;
import marxo.entity.node.Node;
import marxo.entity.user.TenantChildEntity;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Transient;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(value = {
		"nodeIds", "linkIds", "nodes", "links", "startNodeId", "currentNodeId", "endNodeId"
}, ignoreUnknown = true)
public class Workflow extends TenantChildEntity {
	public WorkflowType type = WorkflowType.NONE;
	public List<ObjectId> nodeIds = new ArrayList<>();
	public List<ObjectId> linkIds = new ArrayList<>();
	@Transient
	public List<Node> nodes = new ArrayList<>();
	@Transient
	public List<Link> links = new ArrayList<>();
	public ProjectStatus status = ProjectStatus.IDLE;
	public boolean isProject = false;
	public ObjectId startNodeId;
	public List<ObjectId> currentActionIds = new ArrayList<>();
	@Transient
	public Workflow template;
	ObjectId templateId;

	public Workflow getTemplate() {
		return template;
	}

	public void setTemplate(Workflow template) {
		this.template = template;
		templateId = template.id;
	}

//	public List<ObjectId> getLinkIds() {
//		return (linkIds == null) ? new ArrayList<ObjectId>() : linkIds;
//	}
//
//	public List<ObjectId> getNodeIds() {
//		return (nodeIds == null) ? new ArrayList<ObjectId>() : nodeIds;
//	}
//
//	public List<Node> getNodes() {
//		return (nodes == null) ? new ArrayList<Node>() : nodes;
//	}
//
//	public List<Link> getLinks() {
//		return (links == null) ? new ArrayList<Link>() : links;
//	}

	public void fillWithDefaultValues() {
		super.fillWithDefaultValues();

		if (description == null) {
			description = "";
		}
	}
}
