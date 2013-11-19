package marxo.entity.node;

import com.fasterxml.jackson.annotation.JsonProperty;
import marxo.entity.node.Action;
import marxo.entity.link.Link;
import marxo.entity.workflow.WorkflowChildEntity;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document
public class Node extends WorkflowChildEntity {
	@JsonProperty("offset")
	public Position positoin;
	public List<Action> actions = new ArrayList<>();
	public List<ObjectId> fromlinkIds = new ArrayList<>();
	public List<ObjectId> tolinkIds = new ArrayList<>();
	@Transient
	public List<Link> fromLinks = new ArrayList<>();
	@Transient
	public List<Link> toLinks = new ArrayList<>();

	public Node() {
	}

	public Node(ObjectId workflowId) {
		this.workflowId = workflowId;
	}

	@Override
	public void fillWithDefaultValues() {
		super.fillWithDefaultValues();

		if (actions == null) {
			actions = new ArrayList<>();
		}
	}

	public class Position {
		public double x;
		public double y;
	}
}
