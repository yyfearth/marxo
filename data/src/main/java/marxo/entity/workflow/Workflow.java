package marxo.entity.workflow;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import marxo.entity.BasicEntity;
import marxo.entity.action.MonitorableAction;
import marxo.entity.link.Link;
import marxo.entity.node.Node;
import marxo.entity.user.RunnableEntity;
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
	public boolean isProject = false;

	/*
	Tracable actions
	 */

	public List<ObjectId> tracableActionIds = new ArrayList<>();

	@Transient
	@JsonIgnore
	protected List<MonitorableAction> monitorableActions = new ArrayList<>();

	@JsonIgnore
	public List<MonitorableAction> getMonitorableActions() {
		return monitorableActions;
	}

	@JsonIgnore
	public void setMonitorableActions(List<MonitorableAction> monitorableActions) {
		this.monitorableActions = monitorableActions;
		this.tracableActionIds = new ArrayList<>(Lists.transform(monitorableActions, SelectIdFunction.getInstance()));
	}

	public void addTracableAction(MonitorableAction monitorableAction) {
		monitorableActions.add(monitorableAction);
		tracableActionIds.add(monitorableAction.id);
	}

	/*
	links
	 */

	public List<ObjectId> linkIds = new ArrayList<>();

	@Transient
	protected List<Link> links;

	public List<Link> getLinks() {
		if (linkIds.isEmpty()) {
			return links = new ArrayList<>();
		}
		return (links == null) ? (links = mongoTemplate.find(Query.query(Criteria.where("_id").in(linkIds)), Link.class)) : links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
		this.linkIds = new ArrayList<>(Lists.transform(links, SelectIdFunction.getInstance()));
		for (Link link : links) {
			link.setWorkflow(this);
		}
	}

	public void addLink(Link link) {
		if (links == null) {
			getLinks();
		}
		links.add(link);
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
		if (nodeIds.isEmpty()) {
			return nodes = new ArrayList<>();
		}
		return (nodes == null) ? (nodes = mongoTemplate.find(Query.query(Criteria.where("_id").in(nodeIds)), Node.class)) : nodes;
	}

	public void setNodes(List<Node> nodes) {
		this.nodes = nodes;
		this.nodeIds = new ArrayList<>(Lists.transform(nodes, SelectIdFunction.getInstance()));
		for (Node node : nodes) {
			node.setWorkflow(this);
		}
	}

	public void addNode(Node node) {
		if (nodes == null) {
			getNodes();
		}
		nodes.add(node);
		if (nodes.size() == 1) {
			setStartNode(node);
		}
		nodeIds.add(node.id);
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
		}
		return (startNode == null) ? (startNode = mongoTemplate.findById(startNodeId, Node.class)) : startNode;
	}

	@JsonIgnore
	public void setStartNode(Node startNode) {
		this.startNode = startNode;
		this.startNodeId = (startNode == null) ? null : startNode.id;
	}

	/*
	Template
	 */

	public ObjectId templateId;
	@Transient
	protected Workflow template;

	@JsonIgnore
	public Workflow getTemplate() {
		if (templateId == null) {
			return null;
		}
		return (template == null) ? (template = mongoTemplate.findById(templateId, Workflow.class)) : template;
	}

	@JsonIgnore
	public void setTemplate(Workflow template) {
		this.template = template;
		templateId = template.id;
		this.tenantId = template.tenantId;

//		if (Strings.isNullOrEmpty(this.getName())) {
//			this.setName(template.getName() + " (Copied)");
//		}
//
//		Cloner cloner = new Cloner();
//
//		Map<ObjectId, ObjectId> linkMap = new HashMap<>();
//		for (ObjectId objectId : linkIds) {
//			linkMap.put(objectId, new ObjectId());
//		}
//		Map<ObjectId, ObjectId> nodeMap = new HashMap<>();
//		for (ObjectId objectId : nodeIds) {
//			nodeMap.put(objectId, new ObjectId());
//		}
//
//		// Copy nodes
//		List<Node> nodes = new ArrayList<>();
//		List<Event> events = new ArrayList<>();
//		List<Content> contents = new ArrayList<>();
//		for (Node node : template.getNodes()) {
//			Node newNode = cloner.deepClone(node);
//			newNode.id = nodeMap.get(node.id);
//			newNode.setWorkflow(this);
//			nodes.add(newNode);
//
//			for (ObjectId objectId : newNode.getFromLinkIds()) {
//
//			}
//
//			for (Action action : newNode.getActions()) {
//				action.id = new ObjectId();
//
//				Event event = action.getEvent();
//				if (event != null) {
//					event.id = new ObjectId();
//					event.workflowId = id;
//					events.add(event);
//				}
//
//				Content content = action.getContent();
//				if (content != null) {
//					content.id = new ObjectId();
//					content.workflowId = id;
//					contents.add(content);
//				}
//			}
//		}
//		setNodes(nodes);
//
//		// Copy links
//		List<Link> links = new ArrayList<>();
//		for (Link link : template.getLinks()) {
//			Link newLink = cloner.deepClone(link);
//			newLink.id = linkMap.get(link.id);
//			newLink.setWorkflow(this);
//			if (newLink.previousNodeId != null) {
//				newLink.previousNodeId = nodeMap.get(newLink.previousNodeId);
//			}
//			if (newLink.nextNodeId != null) {
//				newLink.nextNodeId = nodeMap.get(newLink.nextNodeId);
//			}
//			links.add(newLink);
//		}
//		setLinks(links);
//
//		List<BasicEntity> entities = new ArrayList<>();
//		entities.addAll(nodes);
//		entities.addAll(links);
//		entities.addAll(events);
//		entities.addAll(contents);
//
//		mongoTemplate.insertAll(entities);
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
