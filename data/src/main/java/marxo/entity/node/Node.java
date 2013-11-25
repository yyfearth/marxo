package marxo.entity.node;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import marxo.entity.link.Link;
import marxo.entity.workflow.RunStatus;
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
	public List<Action> actions = new ArrayList<>();
	public ObjectId currentActionId;
	public RunStatus status = RunStatus.IDLE;

	public Node() {
	}

	public Node(ObjectId workflowId) {
		this.workflowId = workflowId;
	}

	@SuppressWarnings("SuspiciousMethodCalls")
	@JsonIgnore
	public Action getCurrentAction() {
		if (currentActionId == null) {
			return null;
		}
		int index = actions.indexOf(currentActionId);
		if (index < 0) {
			return null;
		}
		return actions.get(index);
	}

	public List<ObjectId> fromLinkIds = new ArrayList<>();
	public List<ObjectId> toLinkIds = new ArrayList<>();
	@Transient
	protected List<Link> fromLinks = new ArrayList<>();
	@Transient
	protected List<Link> toLinks = new ArrayList<>();

	public List<Link> getFromLinks() {
		if (fromLinkIds == null) {
			return new ArrayList<>();
		}
		Criteria criteria = Criteria.where("id").in(fromLinkIds);
		return fromLinks = mongoTemplate.find(Query.query(criteria), Link.class);
	}

	public void setFromLinks(List<Link> fromLinks) {
		this.fromLinks = fromLinks;
		this.fromLinkIds = Lists.transform(fromLinks, SelectIdFunction.getInstance());
	}

	public List<Link> getToLinks() {
		if (toLinkIds == null) {
			return new ArrayList<>();
		}
		Criteria criteria = Criteria.where("id").in(toLinkIds);
		return toLinks = mongoTemplate.find(Query.query(criteria), Link.class);
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
		return mongoTemplate.findById(id, Node.class);
	}
}
