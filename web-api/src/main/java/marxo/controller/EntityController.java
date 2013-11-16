package marxo.controller;

import marxo.dao.BasicDao;
import marxo.entity.BasicEntity;
import marxo.entity.User;
import marxo.exception.*;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

public abstract class EntityController<Entity extends BasicEntity> extends BasicController implements InterceptorPreHandlable {
	/**
	 * The user who is using the controller.
	 */
	User user;
	BasicDao<Entity> dao;
	@Autowired
	HttpServletRequest request;

	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(HttpStatus.CREATED)
	public Entity create(@Valid @RequestBody Entity entity, HttpServletResponse response) throws Exception {
		entity.createdByUserId = entity.modifiedByUserId = user.id;
		entity.fillWithDefaultValues();
		dao.save(entity);

		response.setHeader("Location", String.format("/%s/%s", entity.getClass().getSimpleName().toLowerCase(), entity.id));
		return entity;
	}

	@RequestMapping(value = "/{idString:[\\da-fA-F]{24}}", method = RequestMethod.GET)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public Entity read(@PathVariable String idString) {
		if (!ObjectId.isValid(idString)) {
			throw new InvalidObjectIdException(idString);
		}

		ObjectId objectId = new ObjectId(idString);
		Entity entity = dao.findOne(objectId);

		if (entity == null) {
			throw new EntityNotFoundException(objectId);
		}

		return entity;
	}

	@RequestMapping(value = "/{idString:[\\da-fA-F]{24}}", method = RequestMethod.PUT)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public Entity update(@Valid @PathVariable String idString, @Valid @RequestBody Entity entity) {
		if (!ObjectId.isValid(idString)) {
			throw new InvalidObjectIdException(idString);
		}

		ObjectId objectId = new ObjectId(idString);
		assert objectId.equals(entity.id);

		Entity oldEntity = dao.findOne(objectId);

		if (oldEntity == null) {
			throw new EntityNotFoundException(objectId);
		}

		try {
			entity.id = oldEntity.id;
			entity.createdByUserId = oldEntity.createdByUserId;
			entity.createdDate = oldEntity.createdDate;
			entity.modifiedByUserId = user.id;
			dao.save(entity);
		} catch (ValidationException ex) {
			for (int i = 0; i < ex.reasons.size(); i++) {
				logger.error(ex.reasons.get(i));
			}
			throw new EntityInvalidException(objectId, ex.reasons);
		}

		return entity;
	}

	@RequestMapping(value = "/{idString:[\\da-fA-F]{24}}", method = RequestMethod.DELETE)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public Entity delete(@PathVariable String idString) {
		if (!ObjectId.isValid(idString)) {
			throw new InvalidObjectIdException(idString);
		}

		ObjectId objectId = new ObjectId(idString);
		Entity entity = dao.deleteById(objectId);

		if (entity == null) {
			throw new EntityNotFoundException(objectId);
		}

		return entity;
	}

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public List<Entity> search() {
		return dao.find();
	}
}
