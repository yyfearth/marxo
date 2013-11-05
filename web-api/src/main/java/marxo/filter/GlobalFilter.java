package marxo.filter;

import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class GlobalFilter extends OncePerRequestFilter {
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		if (logger.isTraceEnabled()) {
			String ip = (request.getHeader("x-real-ip") == null) ? request.getRemoteAddr() : request.getHeader("x-real-ip");
			String port = (request.getHeader("x-real-port") == null) ? String.valueOf(request.getRemotePort()) : request.getHeader("x-real-port");
			String queryString = (request.getQueryString() == null) ? "" : "?" + request.getQueryString();
			logger.trace(String.format("Request from %s %s:%s for %s", request.getMethod(), ip, port, request.getRequestURL() + queryString));
		}
		response.addHeader("Server", "Fucking bad-ass Java-based Tomcat server (epic disasters!)");
		filterChain.doFilter(request, response);
	}
}
