package marxo.validation;

import marxo.entity.MongoDbAware;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public abstract class BasicValidator implements MongoDbAware, Validator {

	@Override
	public void validate(Object target, Errors errors) {
		ValidationUtils.rejectIfEmpty(errors, "id", "id.requires");
	}
}
