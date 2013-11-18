package marxo.controller;

import marxo.dao.LinkDao;
import marxo.entity.link.Link;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("link{:s?}")
public class LinkController extends TenantChildController<Link> {
	@Override
	public void preHandle() {
		super.preHandle();
		dao = new LinkDao(user.tenantId);
	}
}
