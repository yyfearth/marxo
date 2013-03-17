package marxo.restlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.WriteResult;
import marxo.dao.BasicDao;
import marxo.bean.BasicEntity;
import marxo.restlet.exception.EntityNotFoundException;
import marxo.restlet.exception.UnknownException;
import org.bson.types.ObjectId;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.lang.reflect.ParameterizedType;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

/**
 * @param <T> Entity type
 * @param <D> DAO type
 */
public abstract class BasicRestlet<T extends BasicEntity, D extends BasicDao<T>> {

	public static final String ID_PATH = "{id:" + PatternLibrary.ID_PATTERN_STRING + "}";

	protected D dao;

	public BasicRestlet() {
		Class<D> clazz = (Class<D>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];

		try {
			//noinspection unchecked
			this.dao = (D) D.getInstance(clazz);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
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
			throw new UnknownException("Unable to save the entity");
		}

		try {
			return Response.created(new URI(entity.getId().toString())).entity(entity).build();
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return null;
		}
	}

	@GET
	@Path(ID_PATH)
	@Produces(MediaType.APPLICATION_JSON)
	public Response get(@PathParam("id") String id) throws JsonProcessingException {
		T entity = dao.get(new ObjectId(id));

		if (entity == null) {
			throw new EntityNotFoundException();
		}

		return Response.ok(entity).build();
	}

	@PUT
	@Path(ID_PATH)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response set(@PathParam("id") String id, T entity) {
		ObjectId objectId = new ObjectId(id);

		System.out.println("Does exist? " + dao.exists("id", objectId));

		WriteResult writeResult = dao.deleteById(objectId);

		System.out.println("getN: " + writeResult.getN());

		if (writeResult.getError() != null) {
			System.out.println(writeResult.getError());
			throw new UnknownException("The database didn't accept the query.");
		}

		try {
			if (entity.getId() == null) {
				entity.setJsonId(id);
			}

			dao.save(entity);
		} catch (Exception e) {
			e.printStackTrace();
			throw new UnknownException("Unable to save the entity");
		}

		return Response.ok().entity(entity).build();
	}

	@DELETE
	@Path(ID_PATH)
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete(@PathParam("id") String id) {
		String errorMessage = dao.deleteById(new ObjectId(id)).getError();

		if (errorMessage != null) {
			throw new UnknownException("Unable to delete the entity");
		}

		return Response.ok().build();
	}

	public D getDao() {
		return dao;
	}

	public void setDao(D dao) {
		this.dao = dao;
	}
}
