package marxo.entity;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document
public class Node extends WorkflowChildEntity {

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

	class Position {
		public double x;
		public double y;
	}
}
