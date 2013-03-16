package marxo.restlet.exception;

import marxo.restlet.RestletException;
import org.apache.commons.lang.StringUtils;

import javax.ws.rs.core.Response;

public class IdNotProperlyFormattedException extends RestletException {
	public IdNotProperlyFormattedException() {
		this(null);
	}

	public IdNotProperlyFormattedException(String message) {
		errorType = "IdNotProperlyFormatted";
		status = Response.Status.BAD_REQUEST;
		message = "You just gave me garbage as ID?";

		if (StringUtils.isNotEmpty(message)) {
			this.message = message;
		}
	}
}
