package marxo.filter;

import marxo.controller.EntityController;
import marxo.tool.ILoggable;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class MarxoInterceptor extends HandlerInterceptorAdapter implements ILoggable {
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if (handler instanceof HandlerMethod) {
			HandlerMethod handlerMethod = (HandlerMethod) handler;
			logger.debug(String.format("handlerMethod: [%s] %s", handlerMethod.getClass(), handlerMethod.getMethod()));

			if (handlerMethod.getBean() instanceof EntityController) {
				EntityController entityController = (EntityController) handlerMethod.getBean();
				logger.debug(String.format("entityController: [%s]", entityController.getClass()));
			}
		}

		return true;
	}
}
