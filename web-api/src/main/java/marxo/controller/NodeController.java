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
public class NodeController extends GenericController<Node, NodeDao> {
	@Autowired
	public NodeController(NodeDao dao) {
		super(dao);
	}
}
