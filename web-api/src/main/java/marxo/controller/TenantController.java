package marxo.controller;

import marxo.dao.TenantDao;
import marxo.entity.Tenant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("tenant{:s?}")
public class TenantController extends EntityController<Tenant> {
	@Autowired
	public TenantController(TenantDao tenantDao) {
		super(tenantDao);
	}
}
