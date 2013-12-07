package marxo.controller;

import marxo.entity.action.Action;
import marxo.entity.node.Node;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("node{:s?}")
public class NodeController extends TenantChildController<Node> {
	@Override
	public void preHandle() {
		super.preHandle();
	}

	@Override
	public Node read(@PathVariable String idString) throws Exception {
		Node node = super.read(idString);

		for (Action action : node.getActions()) {
			action.getContent();
			action.getEvent();
		}

		return node;
	}
}
