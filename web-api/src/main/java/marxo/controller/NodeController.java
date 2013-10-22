package marxo.controller;

import marxo.entity.Node;
import marxo.dao.NodeDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("node{:s?}")
public class NodeController extends EntityController<Node, NodeDao> {
	@Autowired
	public NodeController(NodeDao dao) {
		super(dao);
	}
}
