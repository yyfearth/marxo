package marxo.restlet.exception;

import marxo.restlet.exception.ErrorJson;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public abstract class RestletException extends WebApplicationException {
	protected String errorType;
	protected Response.Status status;
	protected String message;

	public Response getResponse() {
		return Response.status(status).entity(new ErrorJson(errorType, message)).type(MediaType.APPLICATION_JSON).build();
	}
}
