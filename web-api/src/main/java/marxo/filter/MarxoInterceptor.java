package marxo.filter;

import marxo.controller.TenantChildController;
import marxo.tool.ILoggable;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class MarxoInterceptor extends HandlerInterceptorAdapter implements ILoggable {
	/**
	 * This interceptor calls setupDao if the handling controller is a tenantChildController.
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if (handler instanceof HandlerMethod) {
			HandlerMethod handlerMethod = (HandlerMethod) handler;

			if (handlerMethod.getBean() instanceof TenantChildController) {
				TenantChildController tenantChildController = (TenantChildController) handlerMethod.getBean();
				tenantChildController.setupDao();
			}
		}

		return true;
	}
}
