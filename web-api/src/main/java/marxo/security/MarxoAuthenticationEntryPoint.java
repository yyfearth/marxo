package marxo.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import marxo.exception.ErrorJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

@Component
public class MarxoAuthenticationEntryPoint implements AuthenticationEntryPoint {
	static final Logger logger = LoggerFactory.getLogger(MarxoAuthenticationEntryPoint.class);
	static ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
		ErrorJson errorJson = new ErrorJson("Either you give me your old ID card or you are fired.");
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		response.addHeader("WWW-Authenticate", "Basic realm=\"" + request.getLocalAddr() + "\"");
		response.setContentType(MediaType.APPLICATION_JSON);
		String body = objectMapper.writeValueAsString(errorJson);
		response.getWriter().print(body);
	}
}
