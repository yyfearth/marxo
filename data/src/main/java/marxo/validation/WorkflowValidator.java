package marxo.validation;

import marxo.entity.Workflow;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

// todo: find out how
public class WorkflowValidator implements Validator {
	@Override
	public boolean supports(Class<?> clazz) {
		return Workflow.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		if (!(target instanceof Workflow)) {
			errors.reject("target.is.workflow");
			return;
		}

		Workflow workflow = (Workflow) target;
	}
}
