package marxo.filter;

import marxo.controller.InterceptorPreHandlable;
import marxo.tool.Loggable;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class MarxoInterceptor extends HandlerInterceptorAdapter implements Loggable {
	/**
	 * This interceptor calls preHandle if the handling controller is a tenantChildController.
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if (handler instanceof HandlerMethod) {
			HandlerMethod handlerMethod = (HandlerMethod) handler;
			Object controller = handlerMethod.getBean();

			if (controller instanceof InterceptorPreHandlable) {
				InterceptorPreHandlable interceptorPreHandlable = (InterceptorPreHandlable) controller;
				interceptorPreHandlable.preHandle();
			}
		}

		return true;
	}
}
