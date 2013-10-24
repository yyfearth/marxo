package marxo.controller;

import marxo.dao.LinkDao;
import marxo.entity.Link;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ContextResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("link{:s?}")
public class LinkController extends EntityController<Link, LinkDao> {
	@Autowired
	public LinkController(LinkDao dao) {
		super(dao);
	}
}
