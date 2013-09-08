//package marxo.restlet.filter;
//
//import com.sun.jersey.spi.container.ContainerRequest;
//import com.sun.jersey.spi.container.ContainerResponse;
//import com.sun.jersey.spi.container.ContainerResponseFilter;
//import org.apache.commons.lang3.StringUtils;
//
//import javax.ws.rs.core.MultivaluedMap;
//
//public class ResponseCorsFilter implements ContainerResponseFilter {
//
//	@Override
//	public ContainerResponse filter(ContainerRequest req, ContainerResponse resp) {
//
//		MultivaluedMap<String, Object> headers = resp.getHttpHeaders();
//		headers.add("Access-Control-Allow-Origin", "*");
//		headers.add("Access-Control-Allow-Methods", "*");
//
//		String reqHead = req.getHeaderValue("Access-Control-Request-Headers");
//
//		if (StringUtils.isNotEmpty(reqHead)) {
//			headers.add("Access-Control-Allow-Headers", reqHead);
//		}
//
//		return resp;
//	}
//
//}
