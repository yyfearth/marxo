package marxo.restlet;

import javax.ws.rs.WebApplicationException;

public class ErrorWebApplicationException extends WebApplicationException {
	public ErrorWebApplicationException(ErrorType errorType) {
		this(errorType, null);
	}

	/**
	 * @param errorType
	 * @param message   The message included in the responded JSON
	 */
	public ErrorWebApplicationException(ErrorType errorType, String message) {
		super(ErrorJson.getResponse(errorType, message));
	}
}
