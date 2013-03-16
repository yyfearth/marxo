package marxo.restlet;

import com.mongodb.WriteResult;
import marxo.bean.Node;
import marxo.bean.TenantNode;
import marxo.dao.NodeDao;
import marxo.restlet.exception.EntityNotFoundException;
import marxo.restlet.exception.UnknownException;
import org.bson.types.ObjectId;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

@Path("nodes")
public class NodeRestlet {
	NodeDao nodeDao = new NodeDao();

	@POST
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public Response postNode(TenantNode tenantNode) {
		if (tenantNode == null) {
			tenantNode = new TenantNode();
		}

		tenantNode.setId(new ObjectId());
		Date now = new Date();
		tenantNode.setCreatedDate(now);
		tenantNode.setModifiedDate(now);

		try {
			nodeDao.save(tenantNode);
		} catch (Exception e) {
			e.printStackTrace();
			throw new UnknownException("Unable to save the tenantNode");
		}

		String path = "/" + tenantNode.getId();

		try {
			return Response.created(new URI(path)).entity(tenantNode).build();
		} catch (URISyntaxException e) {
			e.printStackTrace();
			throw new UnknownException("Unable to construct the URI: " + path);
		}
	}

	@GET
	@Path("{nodeId:" + PatternLibrary.ID_PATTERN_STRING + "}")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getNode(@PathParam("nodeId") String nodeId) {
//		if (ObjectId.isValid(nodeId) == false) {
//			throw new RestletException(ErrorType.IdNotProperlyFormatted);
//		}

		Node node = nodeDao.get(new ObjectId(nodeId));

		if (node == null) {
			throw new EntityNotFoundException();
		}

		return Response.ok(node).build();
	}

	@PUT
	@Path("{nodeId:" + PatternLibrary.ID_PATTERN_STRING + "}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public Response putNode(@PathParam("nodeId") String nodeId, TenantNode newTenantNode) {
		System.out.println("Does exist? " + nodeDao.exists("id", nodeId));

		WriteResult writeResult = nodeDao.deleteById(new ObjectId(nodeId));

		System.out.println("getN: " + writeResult.getN());

		if (writeResult.getError() != null) {
			System.out.println(writeResult.getError());
			throw new UnknownException("The database didn't accept the query.");
		}

		try {
			if (newTenantNode.getId() == null) {
				newTenantNode.setJsonId(nodeId);
			}

			nodeDao.save(newTenantNode);
		} catch (Exception e) {
			e.printStackTrace();
			throw new UnknownException("Cannot save the entity.");
		}

		return Response.ok(newTenantNode).build();
	}

	@DELETE
	@Path("{nodeId:" + PatternLibrary.ID_PATTERN_STRING + "}")
	@Produces({MediaType.APPLICATION_JSON})
	public Response deleteNode(@PathParam("nodeId") String nodeId) {
		String errorMessage = nodeDao.deleteById(new ObjectId(nodeId)).getError();

		if (errorMessage != null) {
			throw new UnknownException("Cannot delete the entity.");
		}

		return Response.ok().build();
	}
}
