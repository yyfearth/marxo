package marxo.filter;

import marxo.controller.IInterceptroPreHandlable;
import marxo.tool.ILoggable;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class MarxoInterceptor extends HandlerInterceptorAdapter implements ILoggable {
	/**
	 * This interceptor calls preHandle if the handling controller is a tenantChildController.
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if (handler instanceof HandlerMethod) {
			HandlerMethod handlerMethod = (HandlerMethod) handler;
			Object bean = handlerMethod.getBean();

			if (bean instanceof IInterceptroPreHandlable) {
				IInterceptroPreHandlable interceptroPreHandlable = (IInterceptroPreHandlable) bean;
				interceptroPreHandlable.preHandle();
			}
		}

		return true;
	}
}
