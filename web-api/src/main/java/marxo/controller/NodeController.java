package marxo.controller;

import marxo.bean.Node;
import marxo.dao.NodeDao;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("node{:s?}")
public class NodeController extends BasicController<Node, NodeDao> {

}
