package marxo.controller;

import marxo.entity.action.Action;
import marxo.entity.node.Node;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("node{:s?}")
public class NodeController extends TenantChildController<Node> {
	@Override
	public void preHandle() {
		super.preHandle();
	}

	@Override
	public Node create(@Valid @RequestBody Node node, HttpServletResponse response) throws Exception {

		return super.create(node, response);
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

	@Override
	public Node update(@Valid @PathVariable String idString, @Valid @RequestBody Node node) throws Exception {
		// todo: deal with actions
		return super.update(idString, node);
	}

	@Override
	public Node delete(@PathVariable String idString) throws Exception {
		// todo: remove all actions
		return super.delete(idString);
	}

	@Override
	public List<Node> search() {
		return super.search();
	}
}
