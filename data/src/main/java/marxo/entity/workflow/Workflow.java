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
	@Transient
	protected Node startNode;
	ObjectId templateId;

	public Node getStartNode() {
		return startNode;
	}

	public void setStartNode(Node startNode) {
		this.startNode = startNode;
		this.startNodeId = startNode.id;
	}

	public Workflow getTemplate() {
		return template;
	}

	public void setTemplate(Workflow template) {
		this.template = template;
		templateId = template.id;
	}
}
