package marxo.controller;

import marxo.dao.LinkDao;
import marxo.entity.Link;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("link{:s?}")
public class LinkController extends TenantChildController<Link> {
	@Autowired
	public LinkController(LinkDao linkDao) {
		super(linkDao);
	}
}
