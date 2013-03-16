package marxo.restlet;

import marxo.bean.Link;
import marxo.dao.LinkDao;

import javax.ws.rs.Path;

@Path("links")
public class LinkRestlet extends BasicRestlet<Link, LinkDao> {
	public LinkRestlet() {
		super(new LinkDao());
	}
}
