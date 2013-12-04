package marxo.entity.node;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import marxo.entity.action.Action;
import marxo.entity.link.Link;
import marxo.entity.workflow.WorkflowChildEntity;
import marxo.validation.SelectIdFunction;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.List;

@Document
public class Node extends WorkflowChildEntity {
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
		super.wire();
	}

	/*
	Actions
	 */

	public List<ObjectId> actionIds = new ArrayList<>();

	@Transient
	protected List<Action> actions;

	public List<Action> getActions() {
		if (actionIds.isEmpty()) {
			return actions = new ArrayList<>();
		}
		return (actions == null) ? (actions = mongoTemplate.find(Query.query(Criteria.where("_id").in(actionIds)), Action.class)) : actions;
	}

	public void setActions(List<Action> actions) {
		this.actions = actions;
		this.actionIds = new ArrayList<>(Lists.transform(actions, SelectIdFunction.getInstance()));
	}

	public void addAction(Action action) {
		if (actions == null) {
			if (actionIds.isEmpty()) {
				actions = new ArrayList<>();
			} else {
				actions = mongoTemplate.find(Query.query(Criteria.where("_id").in(actionIds)), Action.class);
			}
		}
		actionIds.add(action.id);
		if (actions.isEmpty()) {
			currentActionId = action.id;
			currentAction = action;
		} else {
			actions.get(actions.size() - 1).setNextAction(action);
		}
		actions.add(action);
		action.setNode(this);
	}

	/*
	Current action
	 */

	protected ObjectId currentActionId;

	public ObjectId getCurrentActionId() {
		if (currentActionId == null) {
			if (actionIds.isEmpty()) {
				return null;
			}
			return currentActionId = actionIds.get(0);
		}
		return currentActionId;
	}

	@Transient
	protected Action currentAction;

	@JsonIgnore
	public Action getCurrentAction() {
		if (getCurrentActionId() == null) {
			return null;
		}
		return (currentAction == null) ? (currentAction = Action.get(getCurrentActionId())) : currentAction;
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
	@JsonIgnore
	protected List<Link> fromLinks;
	@Transient
	@JsonIgnore
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
		this.fromLinkIds = new ArrayList<>(Lists.transform(fromLinks, SelectIdFunction.getInstance()));
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

	@Override
	public void save() {
		super.save();
		if (actions != null) {
			for (Action action : actions) {
				action.save();
			}
		}
	}
}
