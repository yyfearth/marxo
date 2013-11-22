package marxo.controller;

import marxo.entity.node.Node;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("node{:s?}")
public class NodeController extends TenantChildController<Node> {
}
