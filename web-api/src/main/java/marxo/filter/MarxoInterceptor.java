package marxo.filter;

import marxo.controller.EntityController;
import marxo.controller.RestrictedEntityController;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class MarxoInterceptor extends HandlerInterceptorAdapter {
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if (handler instanceof RestrictedEntityController) {
			HandlerMethod handlerMethod = (HandlerMethod) handler;
			EntityController controller = (EntityController) handlerMethod.getBean();
			System.out.println(controller);
		}

		return true;
	}
}
