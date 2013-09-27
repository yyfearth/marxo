package marxo.dao;

import marxo.bean.Workflow;
import marxo.bean.WorkflowValidator;
import marxo.exception.ValidationException;

import java.util.List;

public class WorkflowDao extends BasicDao<Workflow> {

	public void insert(Workflow workflow) {
		mongoTemplate.insert(workflow);
	}

	public void insert(List<Workflow> workflows) {
		mongoTemplate.insert(workflows, eClass);
	}

	@Override
	public void save(Workflow workflow) throws ValidationException {
		List<String> errorReaons = WorkflowValidator.validate(workflow);

		if (errorReaons.size() != 0) {
			throw new ValidationException(errorReaons);
		}

		mongoTemplate.save(workflow);
	}
}
