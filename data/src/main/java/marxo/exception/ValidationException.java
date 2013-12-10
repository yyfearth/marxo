package marxo.exception;

import com.google.common.collect.Lists;

import java.util.List;

// review: might use RunTimeException.
public class ValidationException extends RuntimeException {
	public final List<String> reasons;

	public ValidationException(Errors errors) {
		reasons = errors.getMessages();
	}

	public ValidationException(List<String> reasons) {
		this.reasons = reasons;
	}

	public ValidationException(String reason) {
		this.reasons = Lists.newArrayList(reason);
	}
}
