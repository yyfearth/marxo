package marxo.validation;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class UserValidator extends BasicValidator {
	@Override
	public boolean supports(Class<?> clazz) {
		return false;
	}

	@Override
	public void validate(Object target, Errors errors) {
		super.validate(target, errors);
	}
}
