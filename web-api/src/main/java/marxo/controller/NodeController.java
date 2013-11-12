package marxo.controller;

import marxo.dao.NodeDao;
import marxo.entity.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("node{:s?}")
public class NodeController extends TenantChildController<Node> {
	@Autowired
	public NodeController(NodeDao nodeDao) {
		super(nodeDao);
	}

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public List<Node> search() {
		return dao.findAll();
	}
}
