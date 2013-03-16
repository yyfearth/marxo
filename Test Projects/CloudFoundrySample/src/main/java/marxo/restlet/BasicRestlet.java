package marxo.restlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.jmkgreen.morphia.dao.BasicDAO;
import com.mongodb.WriteResult;
import marxo.bean.BasicEntity;
import org.bson.types.ObjectId;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

public abstract class BasicRestlet<T extends BasicEntity, DAO extends BasicDAO<T, ObjectId>> {

	public static final String ID_PATH = "{id}";

	protected DAO dao;

	public BasicRestlet(DAO dao) {
		this.dao = dao;
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response create(T entity) {
		entity.setId(new ObjectId());
		Date now = new Date();
		entity.setCreatedDate(now);
		entity.setModifiedDate(now);

		try {
			dao.save(entity);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ErrorWebApplicationException(ErrorType.UNKNOWN, "Unable to save the entity");
		}

		try {
			return Response.created(new URI(entity.getId().toString())).entity(entity).build();
		} catch (URISyntaxException e) {
			// impossible
			e.printStackTrace();
			return null;
		}
	}

	@GET
	@Path(ID_PATH)
	@Produces(MediaType.APPLICATION_JSON)
	public Response get(@PathParam("id") String id) throws JsonProcessingException {
		if (!ObjectId.isValid(id)) {
			throw new ErrorWebApplicationException(ErrorType.ID_NOT_PROPERLY_FORMATTED);
		}

		T entity = dao.get(new ObjectId(id));

		if (entity == null) {
			throw new ErrorWebApplicationException(ErrorType.ENTITY_NOT_FOUND);
		}

		return Response.ok(entity).build();
	}

	@PUT
	@Path(ID_PATH)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response set(@PathParam("id") String id, T entity) {
		if (!ObjectId.isValid(id)) {
			throw new ErrorWebApplicationException(ErrorType.ID_NOT_PROPERLY_FORMATTED);
		}

		ObjectId objectId = new ObjectId(id);

		System.out.println("Does exist? " + dao.exists("id", objectId));

		WriteResult writeResult = dao.deleteById(objectId);

		System.out.println("getN: " + writeResult.getN());

		if (writeResult.getError() != null) {
			System.out.println(writeResult.getError());
			throw new ErrorWebApplicationException(ErrorType.UNKNOWN);
		}

		try {
			if (entity.getId() == null) {
				entity.setJsonId(id);
			}

			dao.save(entity);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ErrorWebApplicationException(ErrorType.UNKNOWN);
		}

		return Response.ok().entity(entity).build();
	}

	@DELETE
	@Path(ID_PATH)
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete(@PathParam("id") String id) {
		if (!ObjectId.isValid(id)) {
			throw new ErrorWebApplicationException(ErrorType.ID_NOT_PROPERLY_FORMATTED);
		}

		String errorMessage = dao.deleteById(new ObjectId(id)).getError();

		if (errorMessage != null) {
			throw new ErrorWebApplicationException(ErrorType.UNKNOWN);
		}

		return Response.ok().build();
	}

	public DAO getDao() {
		return dao;
	}

	public void setDao(DAO dao) {
		this.dao = dao;
	}
}
