package marxo.controller;

import marxo.dao.BasicDao;
import marxo.dao.TenantDao;
import marxo.entity.user.Tenant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("tenant{:s?}")
public class TenantController extends EntityController<Tenant> {
	@Autowired
	protected TenantController(TenantDao dao) {
		super(dao);
	}

	@Override
	public void preHandle() {
	}
}
