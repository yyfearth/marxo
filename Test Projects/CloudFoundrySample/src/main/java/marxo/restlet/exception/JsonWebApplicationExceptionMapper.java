package marxo.restlet.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class JsonWebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {
	@Override
	public Response toResponse(WebApplicationException e) {
		e.printStackTrace();
		Response response = e.getResponse();
		ErrorJson errorJson = new ErrorJson(e.getClass().getSimpleName(), e.getMessage());
		return Response.status(response.getStatus()).entity(errorJson).type(MediaType.APPLICATION_JSON).build();
	}
}
