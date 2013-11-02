package marxo.controller;

import marxo.tool.ILoggable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

public abstract class BasicController implements ILoggable {
	@Autowired
	ApplicationContext applicationContext;
}
