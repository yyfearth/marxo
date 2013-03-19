package marxo.restlet.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import marxo.data.JsonParser;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

/**
 * Use our own configured object mapper. Read:
 * <a href="http://jersey.java.net/nonav/documentation/latest/user-guide.html#d4e928">http://jersey.java.net/nonav/documentation/latest/user-guide.html#d4e928</a>
 */
@Provider
public class ObjectMapperProvider implements ContextResolver<ObjectMapper> {
	@Override
	public ObjectMapper getContext(Class<?> aClass) {
		return JsonParser.getMapper();
	}
}
