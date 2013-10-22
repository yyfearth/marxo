package marxo.entity;

import java.util.ArrayList;
import java.util.List;

public class WorkflowValidator {
	public static List<String> validate(Workflow workflow) {
		List<String> reasons = new ArrayList<>();

		if (workflow == null) {
			throw new NullPointerException("workflow is null");
		}

		if (workflow.tenantId == null) {
			reasons.add("Tenant ID cannot be null");
		}

//		if (StringUtils.isEmpty(workflow.title)) {
//			reasons.add("Title cannot be empty.");
//		}

		reasons.addAll(validateGraph(workflow));
		reasons.addAll(validateUsers(workflow));
		reasons.addAll(validateDate(workflow));

		return reasons;
	}

	protected static List<String> validateUsers(Workflow workflow) {
		List<String> reasons = new ArrayList<>();

		if (workflow.createdByUserId == null) {
			reasons.add("Creating user ID cannot be null.");
		}

		if (workflow.modifiedByUserId == null) {
			reasons.add("Modifying user ID cannot be null.");
		}

		// todo: check the users exist or not

		return reasons;
	}

	protected static List<String> validateDate(Workflow workflow) {
		List<String> reasons = new ArrayList<>();

		if (workflow.createdDate == null) {
			reasons.add("created date cannot be null.");
		}

		if (workflow.modifiedDate == null) {
			reasons.add("modified date cannot be null.");
		}

		return reasons;
	}

	protected static List<String> validateGraph(Workflow workflow) {
		List<String> reasons = new ArrayList<>();
		return reasons;
	}
}
