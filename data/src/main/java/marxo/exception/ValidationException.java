package marxo.exception;

import java.util.List;

// review: might use RunTimeException.
public class ValidationException extends RuntimeException {
	public final List<String> reasons;

	public ValidationException(List<String> reasons) {
		this.reasons = reasons;
	}
}
