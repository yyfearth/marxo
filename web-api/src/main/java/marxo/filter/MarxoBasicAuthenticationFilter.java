package marxo.filter;

import marxo.dao.UserDao;
import marxo.security.MarxoAuthentication;
import marxo.tool.PasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class MarxoBasicAuthenticationFilter extends BasicAuthenticationFilter {
	@Autowired
	UserDao userDao;
	@Autowired
	PasswordEncryptor passwordEncryptor;

	// The constructor is unnecessary. But it's here because Spring complains about deprecated constructor.
	@Autowired
	public MarxoBasicAuthenticationFilter(AuthenticationManager authenticationManager) {
		super(authenticationManager);
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		super.doFilter(req, res, chain);
	}

	@Override
	protected void onSuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, Authentication authResult) throws IOException {
		MarxoAuthentication marxoAuthentication = (MarxoAuthentication) authResult;
		request.setAttribute("user", marxoAuthentication.getUser());
		super.onSuccessfulAuthentication(request, response, authResult);
	}

	@Override
	protected void onUnsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
		super.onUnsuccessfulAuthentication(request, response, failed);
	}
}
