package marxo.controller;

import marxo.dao.NodeDao;
import marxo.entity.node.Node;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("node{:s?}")
public class NodeController extends TenantChildController<Node> {
	@Override
	public void preHandle() {
		super.preHandle();
		dao = new NodeDao(user.tenantId);
	}
}
