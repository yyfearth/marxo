package marxo.entity.workflow;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import marxo.entity.BasicEntity;
import marxo.entity.RunnableEntity;
import marxo.entity.link.Link;
import marxo.entity.node.Node;
import marxo.validation.SelectIdFunction;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(value = {
}, ignoreUnknown = true)
public class Workflow extends RunnableEntity {
	public WorkflowType type = WorkflowType.NONE;
	public boolean isProject = false;

	/*
	links
	 */

	public List<ObjectId> linkIds = new ArrayList<>();

	@Transient
	protected List<Link> links;

	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
		this.linkIds = Lists.transform(links, SelectIdFunction.getInstance());
		for (Link link : links) {
			link.tenantId = tenantId;
			link.workflowId = id;
		}
	}

	public void addLink(Link link) {
		if (links != null) {
			links.add(link);
		}
		linkIds.add(link.id);
		link.setWorkflow(this);
	}

	/*
	Nodes
	 */

	public List<ObjectId> nodeIds = new ArrayList<>();

	@Transient
	protected List<Node> nodes;

	public List<Node> getNodes() {
		return nodes;
	}

	public void setNodes(List<Node> nodes) {
		this.nodes = nodes;
		this.nodeIds = Lists.transform(nodes, SelectIdFunction.getInstance());
		for (Node node : nodes) {
			node.tenantId = tenantId;
			node.workflowId = id;
		}
	}

	public void addNode(Node node) {
		if (nodes != null) {
			nodes.add(node);
		}
		nodeIds.add(node.id);
		if (nodeIds.size() == 1) {
			startNodeId = node.id;
		}
		node.setWorkflow(this);
	}

	/*
	Start node
	 */

	public ObjectId startNodeId;

	@Transient
	protected Node startNode;

	@JsonIgnore
	public Node getStartNode() {
		if (startNodeId == null) {
			if (nodeIds.isEmpty()) {
				return null;
			}
			wire();

		}
		if (startNode == null) {
			return startNode = mongoTemplate.findById(startNodeId, Node.class);
		}
		return startNode;
	}

	@JsonIgnore
	public void setStartNode(Node startNode) {
		this.startNode = startNode;
		this.startNodeId = (startNode == null) ? null : startNode.id;
	}

	/*
	Template
	 */

	@Transient
	protected Workflow template;
	public ObjectId templateId;

	@JsonIgnore
	public Workflow getTemplate() {
		return template;
	}

	@JsonIgnore
	public void setTemplate(Workflow template) {
		this.template = template;
		templateId = template.id;
	}

	/*
	Current nodes
	 */

	public List<ObjectId> currentNodeIds = new ArrayList<>();

	@Transient
	protected List<Node> currentNodes;

	@JsonIgnore
	public List<Node> getCurrentNodes() {
		if (currentNodes == null) {
			if (currentNodeIds.isEmpty()) {
				return currentNodes = new ArrayList<>();
			}

			Criteria criteria = Criteria.where("id").in(currentNodeIds);
			currentNodes = mongoTemplate.find(Query.query(criteria), Node.class);
			wire();
		}
		return currentNodes;
	}

	public void addCurrentNode(Node node) {
		if (currentNodes == null) {
			getCurrentNodes();
		}
		currentNodes.add(node);
		currentNodeIds.add(node.id);
		node.setWorkflow(this);
	}

	/*
	Validation/wire
	 */

	@Override
	public void wire() {
		if (nodes == null || links == null) {
			return;
		}

		Map<ObjectId, Node> nodeMap = Maps.uniqueIndex(nodes, SelectIdFunction.getInstance());

		boolean isOkay = true;

		// Set start node and next nodes.
		if (nodes.isEmpty()) {
			startNodeId = null;
		} else {
			for (Link link : links) {
				Node fromNode = nodeMap.get(link.previousNodeId);
				Node toNode = nodeMap.get(link.nextNodeId);
				fromNode.getToLinkIds().add(link.id);
				fromNode.getToNodeIds().add(toNode.id);
				toNode.getFromLinkIds().add(link.id);
				toNode.getFromNodeIds().add(fromNode.id);

				link.setPreviousNode(fromNode);
				link.setNextNode(toNode);
			}

			startNodeId = null;
			for (Node node : nodes) {
				node.wire();

				if (node.getFromNodeIds().isEmpty() && !node.id.equals(startNodeId)) {
					if (startNodeId == null) {
						startNodeId = node.id;
					} else {
						logger.debug(String.format("Workflow [%s] validation error: node [%s] is also a potential start node", id, node.id));
						isOkay = false;
					}
				}
			}
			setStartNode(nodeMap.get(startNodeId));
		}

		this.isValidated = isOkay;
	}

	@Override
	public void deepWire() {
		Query query = Query.query(Criteria.where("workflowId").is(id));
		if (nodes == null) {
			nodes = mongoTemplate.find(query, Node.class);
		}
		if (links == null) {
			links = mongoTemplate.find(query, Link.class);
		}

		Map<ObjectId, Node> nodeMap = Maps.uniqueIndex(nodes, SelectIdFunction.getInstance());

		boolean isOkay = true;

		// Set start node and next nodes.
		if (nodes.isEmpty()) {
			startNodeId = null;
		} else {
			for (Link link : links) {
				Node fromNode = nodeMap.get(link.previousNodeId);
				Node toNode = nodeMap.get(link.nextNodeId);
				fromNode.getToLinkIds().add(link.id);
				fromNode.getToNodeIds().add(toNode.id);
				toNode.getFromLinkIds().add(link.id);
				toNode.getFromNodeIds().add(fromNode.id);

				link.setPreviousNode(fromNode);
				link.setNextNode(toNode);
			}

			startNodeId = null;
			for (Node node : nodes) {
				node.wire();

				if (node.getFromNodeIds().isEmpty() && !node.id.equals(startNodeId)) {
					if (startNodeId == null) {
						startNodeId = node.id;
					} else {
						logger.debug(String.format("Workflow [%s] validation error: node [%s] is also a potential start node", id, node.id));
						isOkay = false;
					}
				}
			}
			setStartNode(nodeMap.get(startNodeId));
		}

		this.isValidated = isOkay;

		mongoTemplate.remove(this);

		Criteria criteria = Criteria.where("_id").in(nodeIds);
		mongoTemplate.remove(Query.query(criteria), Node.class);

		criteria = Criteria.where("_id").in(linkIds);
		mongoTemplate.remove(Query.query(criteria), Link.class);

		List<BasicEntity> entitiesToSave = new ArrayList<>();
		entitiesToSave.add(this);
		entitiesToSave.addAll(nodes);
		entitiesToSave.addAll(links);
		mongoTemplate.insertAll(entitiesToSave);
	}

	/*
	DAO
	 */

	public static Workflow get(ObjectId id) {
		Workflow workflow = mongoTemplate.findById(id, Workflow.class);
		if (workflow != null) {
			workflow.wire();
		}
		return workflow;
	}
}
