package marxo.restlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.WriteResult;
import marxo.bean.Node;
import marxo.bean.TenantNode;
import marxo.dao.NodeDao;
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
		if (tenantNode.getTemplateId() == null) {
			throw new ErrorWebApplicationException(ErrorType.InvalidRequest, "No template id");
		}

		tenantNode.setId(new ObjectId());
		Date now = new Date();
		tenantNode.setCreatedDate(now);
		tenantNode.setModifiedDate(now);

		try {
			nodeDao.save(tenantNode);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ErrorWebApplicationException(ErrorType.Unknown, "Unable to save the tenantNode");
		}

		String path = "/" + tenantNode.getId();

		try {
			return Response.created(new URI(path)).entity(tenantNode).build();
		} catch (URISyntaxException e) {
			e.printStackTrace();
			throw new ErrorWebApplicationException(ErrorType.Unknown, "Unable to construct the URI: " + path);
		}
	}

	@GET
	@Path("{nodeId}")
	@Produces({MediaType.APPLICATION_JSON})
	public Response getNode(@PathParam("nodeId") String nodeId) throws JsonProcessingException {
		if (ObjectId.isValid(nodeId) == false) {
			throw new ErrorWebApplicationException(ErrorType.IdNotProperlyFormatted);
		}

		Node node = nodeDao.get(new ObjectId(nodeId));

		if (node == null) {
			throw new ErrorWebApplicationException(ErrorType.EntityNotFound);
		}

		return Response.ok(node).build();
	}

	@PUT
	@Path("{nodeId}")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public Response putNode(@PathParam("nodeId") String nodeId, TenantNode newTenantNode) {
		if (ObjectId.isValid(nodeId) == false) {
			throw new ErrorWebApplicationException(ErrorType.IdNotProperlyFormatted);
		}

		System.out.println("Does exist? " + nodeDao.exists("id", nodeId));

		WriteResult writeResult = nodeDao.deleteById(new ObjectId(nodeId));

		System.out.println("getN: " + writeResult.getN());

		if (writeResult.getError() != null) {
			System.out.println(writeResult.getError());
			throw new ErrorWebApplicationException(ErrorType.Unknown);
		}

		try {
			if (newTenantNode.getId() == null) {
				newTenantNode.setJsonId(nodeId);
			}

			nodeDao.save(newTenantNode);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ErrorWebApplicationException(ErrorType.Unknown);
		}

		return Response.ok(newTenantNode).build();
	}

	@DELETE
	@Path("{nodeId}")
	@Produces({MediaType.APPLICATION_JSON})
	public Response deleteNode(@PathParam("nodeId") String nodeId) {
		if (ObjectId.isValid(nodeId) == false) {
			throw new ErrorWebApplicationException(ErrorType.IdNotProperlyFormatted);
		}

		String errorMessage = nodeDao.deleteById(new ObjectId(nodeId)).getError();

		if (errorMessage != null) {
			throw new ErrorWebApplicationException(ErrorType.Unknown);
		}

		return Response.ok().build();
	}
}
