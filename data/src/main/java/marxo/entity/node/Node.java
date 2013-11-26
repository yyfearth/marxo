package marxo.entity.node;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import marxo.entity.link.Link;
import marxo.entity.workflow.RunStatus;
import marxo.entity.workflow.WorkflowChildEntity;
import marxo.validation.NodeValidator;
import marxo.validation.SelectIdFunction;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Document
public class Node extends WorkflowChildEntity {
	public RunStatus status = RunStatus.IDLE;

	public Node() {
	}

	public Node(ObjectId workflowId) {
		this.workflowId = workflowId;
	}

	/*
	From/to nodes
	 */

	protected List<ObjectId> toNodeIds = new ArrayList<>();

	public List<ObjectId> getToNodeIds() {
		return toNodeIds;
	}

	public void setToNodeIds(List<ObjectId> toNodeIds) {
		this.toNodeIds = toNodeIds;
	}

	protected List<ObjectId> fromNodeIds = new ArrayList<>();

	public List<ObjectId> getFromNodeIds() {
		return fromNodeIds;
	}

	public void setFromNodeIds(List<ObjectId> fromNodeIds) {
		this.fromNodeIds = fromNodeIds;
	}

	/*
	Wire
	 */

	@Override
	public void wire() {
		for (int i = 0, len = actions.size(); i < len; i++) {
			Action action = actions.get(i);
			action.setNode(this);
			if (i + 1 != len) {
				action.setNextAction(actions.get(i + 1));
			}

			action.setNode(this);
			action.tenantId = tenantId;
		}
	}

	/*
	Actions
	 */

	protected List<Action> actions = new ArrayList<>();

	public List<Action> getActions() {
		return actions;
	}

	// review: could be optimized
	public void setActions(List<Action> actions) {
		this.actions = actions;
		NodeValidator.wire(this);
	}

	@Transient
	Map<ObjectId, Action> actionMap;

	public Map<ObjectId, Action> getActionMap() {
		return (actionMap == null) ? (actionMap = Maps.uniqueIndex(actions, SelectIdFunction.getInstance())) : actionMap;
	}

	public Action getFirstAction() {
		return (actions.isEmpty()) ? null : actions.get(0);
	}

	/*
	Current action
	 */

	protected ObjectId currentActionId;

	public ObjectId getCurrentActionId() {
		return currentActionId;
	}

	@Transient
	protected Action currentAction;

	public void setCurrentActionId(ObjectId currentActionId) {
		this.currentActionId = currentActionId;
		currentAction = getActionMap().get(currentActionId);
	}

	@JsonIgnore
	public Action getCurrentAction() {
		if (currentActionId == null) {
			return null;
		}
		return (currentAction == null) ? (currentAction = getActionMap().get(currentActionId)) : null;
	}

	public void setCurrentAction(Action currentAction) {
		this.currentAction = currentAction;
		this.currentActionId = currentAction.id;
	}

	/*
	To/from links
	 */

	protected List<ObjectId> fromLinkIds = new ArrayList<>();

	public List<ObjectId> getFromLinkIds() {
		return fromLinkIds;
	}

	public void setFromLinkIds(List<ObjectId> fromLinkIds) {
		this.fromLinkIds = fromLinkIds;
	}

	protected List<ObjectId> toLinkIds = new ArrayList<>();

	public List<ObjectId> getToLinkIds() {
		return toLinkIds;
	}

	public void setToLinkIds(List<ObjectId> toLinkIds) {
		this.toLinkIds = toLinkIds;
	}

	@Transient
	protected List<Link> fromLinks;
	@Transient
	protected List<Link> toLinks;

	public List<Link> getFromLinks() {
		if (fromLinks == null) {
			Criteria criteria = Criteria.where("id").in(fromLinkIds);
			return fromLinks = mongoTemplate.find(Query.query(criteria), Link.class);
		}
		return fromLinks;
	}

	public void setFromLinks(List<Link> fromLinks) {
		this.fromLinks = fromLinks;
		this.fromLinkIds = Lists.transform(fromLinks, SelectIdFunction.getInstance());
	}

	public List<Link> getToLinks() {
		if (toLinks == null) {
			Criteria criteria = Criteria.where("id").in(toLinkIds);
			return toLinks = mongoTemplate.find(Query.query(criteria), Link.class);
		}
		return toLinks;
	}

	public void setToLinks(List<Link> toLinks) {
		this.toLinks = toLinks;
	}

	/*
	Position
	 */

	@JsonProperty("offset")
	public Position positoin;

	public class Position {
		public double x;
		public double y;
	}

	/*
	DAO
	 */

	public static Node get(ObjectId id) {
		Node node = mongoTemplate.findById(id, Node.class);
		if (node != null) {
			node.wire();
		}
		return node;
	}
}
