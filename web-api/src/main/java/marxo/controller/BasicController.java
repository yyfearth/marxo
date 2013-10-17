package marxo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.annotation.PostConstruct;

public abstract class BasicController {
	final Logger logger = LoggerFactory.getLogger(BasicController.class);
	@Autowired
	ApplicationContext applicationContext;

	@PostConstruct
	void report() {
		Boolean isDebug = applicationContext.getBean("isDebug", Boolean.class);
		isDebug = (isDebug == null) ? false : isDebug;

		if (isDebug) {
			logger.debug(TestController.class.getSimpleName() + " started");
			// Prevent the JVM to prompt OutOfMemory while IntelliJ redeploys the app. (fuck dat JVM)
//			System.gc();
		}
	}
}
