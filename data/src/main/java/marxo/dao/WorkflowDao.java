package marxo.dao;

import com.google.common.base.Strings;
import marxo.entity.Workflow;
import marxo.entity.WorkflowValidator;
import marxo.exception.ValidationException;
import marxo.tool.StringTool;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository("workflowDao")
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

	public List<Workflow> searchByName(String name) {
		if (Strings.isNullOrEmpty(name)) {
			return new ArrayList<>();
		}
		String escapedName = StringTool.escapePatternCharacters(name);
		return mongoTemplate.find(Query.query(Criteria.where("name").regex(".*" + escapedName + ".*")), Workflow.class);
	}
}
