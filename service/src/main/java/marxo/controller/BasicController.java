package marxo.controller;

import marxo.bean.BasicEntity;
import marxo.dao.BasicDao;
import org.springframework.stereotype.Controller;

@Controller
public class BasicController<Entity extends BasicEntity, Dao extends BasicDao<Entity>> {

}
