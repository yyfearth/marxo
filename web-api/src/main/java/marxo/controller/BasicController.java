package marxo.controller;

import marxo.bean.Entity;
import marxo.dao.BasicDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.annotation.PostConstruct;

public class BasicController<E extends Entity, Dao extends BasicDao<E>> {
	final Logger logger = LoggerFactory.getLogger(BasicController.class);
	@Autowired
	ApplicationContext applicationContext;

	@PostConstruct
	void report() {
		logger.info(this.getClass().getSimpleName() + " started");
	}
}
