package marxo.restlet.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
public class UnknownException extends RestletException {
	public UnknownException(String message) {
		errorType = "Uknown";
		status = Response.Status.INTERNAL_SERVER_ERROR;
		message = "Someone calls 911!!";
		this.message = message;
	}
}
