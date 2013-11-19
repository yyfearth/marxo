package marxo.controller;

import marxo.dao.BasicDao;
import marxo.dao.NodeDao;
import marxo.entity.node.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("node{:s?}")
public class NodeController extends TenantChildController<Node> {
	@Autowired
	protected NodeController(NodeDao dao) {
		super(dao);
	}
}
