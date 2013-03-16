package marxo.restlet.exception;

import marxo.restlet.RestletException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class RestletExceptionMapper implements ExceptionMapper<RestletException> {
	@Override
	public Response toResponse(RestletException e) {
		return e.getResponse();
	}
}
