package marxo.controller;

import marxo.dao.LinkDao;
import marxo.entity.Link;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("link{:s?}")
public class LinkController extends TenantChildController<Link> {
	@Override
	public void preHandle() {
		super.preHandle();
		dao = new LinkDao(user.tenantId);
	}
}
