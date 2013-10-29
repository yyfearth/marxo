package marxo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

public abstract class BasicController {
	final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	ApplicationContext applicationContext;
}
