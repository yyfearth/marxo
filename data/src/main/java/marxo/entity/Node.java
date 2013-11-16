package marxo.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import marxo.entity.action.Action;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document
public class Node extends WorkflowChildEntity {
	@JsonProperty("offset")
	public Position positoin;
	public List<Action> actions;

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
