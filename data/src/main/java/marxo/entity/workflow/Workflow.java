package marxo.entity.workflow;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import marxo.entity.link.Link;
import marxo.entity.node.Node;
import marxo.entity.user.TenantChildEntity;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@JsonIgnoreProperties(value = {
}, ignoreUnknown = true)
public class Workflow extends TenantChildEntity {
	public WorkflowType type = WorkflowType.NONE;
	public Set<ObjectId> nodeIds = new HashSet<>();
	public Set<ObjectId> linkIds = new HashSet<>();
	@Transient
	public List<Node> nodes = new ArrayList<>();
	@Transient
	public List<Link> links = new ArrayList<>();
	public RunStatus status = RunStatus.IDLE;
	public boolean isProject = false;
	public ObjectId startNodeId;
	public List<ObjectId> currentNodeIds = new ArrayList<>();
	@Transient
	public Workflow template;
	@Transient
	protected Node startNode;
	ObjectId templateId;

	@JsonIgnore
	public Node getStartNode() {
		return startNode;
	}

	@JsonIgnore
	public void setStartNode(Node startNode) {
		this.startNode = startNode;
		this.startNodeId = startNode.id;
	}

	@JsonIgnore
	public Workflow getTemplate() {
		return template;
	}

	@JsonIgnore
	public void setTemplate(Workflow template) {
		this.template = template;
		templateId = template.id;
	}

	@JsonIgnore
	public List<Node> getCurrentNodes() {
		Criteria criteria = Criteria.where("id").in(currentNodeIds);
		return mongoTemplate.find(Query.query(criteria), Node.class);
	}

	public static Workflow get(ObjectId id) {
		return mongoTemplate.findById(id, Workflow.class);
	}
}
