package marxo.controller;

import marxo.entity.Tenant;
import marxo.dao.TenantDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("tenant{:s?}")
public class TenantController extends EntityController<Tenant, TenantDao> {
	@Autowired
	public TenantController(TenantDao dao) {
		super(dao);
	}
}
