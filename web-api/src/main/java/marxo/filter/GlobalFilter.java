package marxo.filter;

import com.google.common.base.Strings;
import marxo.tool.Loggable;
import net.sf.uadetector.ReadableUserAgent;
import net.sf.uadetector.UserAgentStringParser;
import net.sf.uadetector.service.UADetectorServiceFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class GlobalFilter extends OncePerRequestFilter implements Loggable {
	static UserAgentStringParser userAgentStringParser = UADetectorServiceFactory.getResourceModuleParser();

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		if (Loggable.logger.isDebugEnabled()) {
			String ip = (request.getHeader("x-real-ip") == null) ? request.getRemoteAddr() : request.getHeader("x-real-ip");
			String port = (request.getHeader("x-real-port") == null) ? String.valueOf(request.getRemotePort()) : request.getHeader("x-real-port");

			String queryString = (request.getQueryString() == null) ? "" : "?" + request.getQueryString();

			String userAgent = request.getHeader("User-Agent");
			String clientString;
			if (Strings.isNullOrEmpty(userAgent.trim())) {
				clientString = "N/A";
			} else {
				ReadableUserAgent agent = userAgentStringParser.parse(userAgent);
				clientString = String.format(
						"%s %s with %s %s",
						agent.getOperatingSystem().getFamilyName(),
						agent.getOperatingSystem().getVersionNumber().toVersionString(),
						agent.getFamily(),
						agent.getVersionNumber().getMajor()
				);
			}

			Loggable.logger.debug(String.format("%s request from %s:%s(%s) for %s", request.getMethod(), ip, port, clientString, request.getRequestURL() + queryString));
		}
		response.addHeader("Server", "Fucking bad-ass Java-based Tomcat server (epic disasters!)");
		response.addHeader("Version", "0.6");
		filterChain.doFilter(request, response);
	}
}
