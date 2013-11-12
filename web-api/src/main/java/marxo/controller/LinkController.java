package marxo.controller;

import marxo.dao.LinkDao;
import marxo.entity.Link;
import marxo.entity.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("link{:s?}")
public class LinkController extends TenantChildController<Link> {
	@Autowired
	public LinkController(LinkDao linkDao) {
		super(linkDao);
	}

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public List<Link> search() {
		return dao.findAll();
	}
}
