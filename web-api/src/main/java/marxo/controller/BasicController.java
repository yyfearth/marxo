package marxo.controller;

import marxo.bean.BasicEntity;
import marxo.dao.BasicDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

@Controller
public class BasicController<Entity extends BasicEntity, Dao extends BasicDao<Entity>> {
	final Logger logger = LoggerFactory.getLogger(BasicController.class);
}
