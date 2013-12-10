package marxo.controller;

import marxo.tool.Loggable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

public abstract class BasicController implements Loggable {
	@Autowired
	ApplicationContext applicationContext;
}
