package marxo.controller;

import marxo.bean.Node;
import marxo.dao.NodeDao;
import marxo.exception.*;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("node{:s?}")
public class NodeController extends BasicController<Node, NodeDao> {
	@Autowired
	NodeDao nodeDao;

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public List<Node> getAll() {
		return nodeDao.findAll();
	}

	@RequestMapping(value = "/", method = RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(HttpStatus.CREATED)
	public Node create(@Valid @RequestBody Node node) throws Exception {
		if (nodeDao.exists(node.getId())) {
			throw new EntityExistsException(node.getId());
		}

		try {
			nodeDao.save(node);
		} catch (ValidationException ex) {
			// todo: add error message
			throw new EntityInvalidException(node.getId(), "not implemented");
		}

		return node;
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public Node read(@PathVariable String id) {
		if (!ObjectId.isValid(id)) {
			throw new InvalidObjectIdException(id);
		}

		ObjectId objectId = new ObjectId(id);
		Node node = nodeDao.get(objectId);

		if (node == null) {
			throw new EntityNotFoundException(objectId);
		}

		return node;
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	// fixme: get ObjectId from Spring MVC, and let the global validator do the validation.
	// review: is the parameter 'id' necessary?
	public Node update(@Valid @PathVariable String id, @Valid @RequestBody Node node) {
		if (!ObjectId.isValid(id)) {
			throw new InvalidObjectIdException(id);
		}

		ObjectId objectId = new ObjectId(id);

		// todo: check consistency of given id and node id.
		Node oldWorkflow = nodeDao.get(objectId);

		if (oldWorkflow == null) {
			throw new EntityNotFoundException(objectId);
		}

		try {
			nodeDao.save(oldWorkflow);
		} catch (ValidationException e) {
//			e.reasons.toString()
//			throw new EntityInvalidException(objectId, );
		}

		return oldWorkflow;
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	// fixme: get ObjectId from Spring MVC, and let the global validator do the validation.
	public Node delete(@PathVariable String id) {
		if (!ObjectId.isValid(id)) {
			throw new InvalidObjectIdException(id);
		}

		ObjectId objectId = new ObjectId(id);
		Node node = nodeDao.deleteById(objectId);

		if (node == null) {
			throw new EntityNotFoundException(objectId);
		}

		return node;
	}
}
