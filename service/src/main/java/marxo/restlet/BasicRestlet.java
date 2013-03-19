package marxo.restlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.WriteResult;
import marxo.bean.BasicEntity;
import marxo.dao.BasicDao;
import marxo.restlet.exception.EntityNotFoundException;
import marxo.restlet.exception.UnknownException;
import org.apache.commons.lang.StringUtils;
import org.bson.types.ObjectId;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;

/**
 * @param <E> Entity type
 * @param <D> DAO type
 */
public abstract class BasicRestlet<E extends BasicEntity, D extends BasicDao<E>> implements Restlet {

	public static final String ID_PATTERN_STRING = "[\\da-fA-F]{24}";
	public static final String ID_PATH = "{id:" + ID_PATTERN_STRING + "}";

	// @Context ServletContext context;
	@Context
	HttpServletRequest request;

	protected D dao;

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response create(E entity) {
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
	@Produces(MediaType.APPLICATION_JSON)
	public List<E> findAll() throws JsonProcessingException {
		return dao.findAll();
	}

	@GET
	@Path(ID_PATH)
	@Produces(MediaType.APPLICATION_JSON)
	public E get(@PathParam("id") String id) throws JsonProcessingException {
		E entity = dao.get(new ObjectId(id));

		if (entity == null) {
			throw new EntityNotFoundException();
		}

		return entity; // 200 (OK)
	}

	@PUT
	@Path(ID_PATH)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public E set(@PathParam("id") String id, E entity) {
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

			Date now = new Date();
			if (entity.getCreatedDate() == null) {
				entity.setCreatedDate(now);
			}
			entity.setModifiedDate(now);

			dao.save(entity);
		} catch (Exception e) {
			e.printStackTrace();
			throw new UnknownException("Unable to save the entity");
		}

		return entity; // 200 (OK)
	}

	@DELETE
	@Path(ID_PATH)
	@Produces(MediaType.APPLICATION_JSON)
	public void delete(@PathParam("id") String id) {
		String errorMessage = dao.deleteById(new ObjectId(id)).getError();

		if (errorMessage != null) {
			throw new UnknownException("Unable to delete the entity");
		}

		// no return will be 204 (No Content) if succeed
	}

	// to get parameter in url
	public String getParameter(String paramName){
		return request.getParameter(paramName);
	}

	// to get boolean parameter in url
	public boolean hasFlag(String paramName) {
		return hasFlag(paramName, false);
	}

	// to get boolean parameter in url
	public boolean hasFlag(String paramName, boolean defVal) {
		if (request == null) {
			return defVal;
		}
		String reqParam = getParameter(paramName);
		if (StringUtils.isEmpty(reqParam)) {
			return defVal;
		} else if (!defVal) { // default false
			return reqParam.matches("(?i)true|yes|on|1");
		} else { // default true
			return !reqParam.matches("(?i)false|no|off|0");
		}
	}

	public D getDao() {
		return dao;
	}

	public void setDao(D dao) {
		this.dao = dao;
	}
}
