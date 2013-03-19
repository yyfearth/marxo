package marxo.restlet;

import marxo.bean.Node;
import marxo.dao.NodeDao;

import javax.ws.rs.Path;

@Path("nodes")
public class NodeRestlet extends BasicRestlet<Node, NodeDao> {

}
