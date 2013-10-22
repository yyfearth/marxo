package marxo.controller;

import marxo.dao.BasicDao;
import marxo.entity.BasicEntity;
import marxo.exception.*;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;

public abstract class EntityController<E extends BasicEntity, Dao extends BasicDao<E>> extends BasicController {
	Dao dao;

	protected EntityController(Dao dao) {
		this.dao = dao;
	}

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public List<E> getAll(@RequestParam(required = false) String name, @RequestParam(required = false) Date modified, @RequestParam(required = false) Date created) {
		return dao.findAll();
	}

	@RequestMapping(value = "/", method = RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(HttpStatus.CREATED)
	public E create(@Valid @RequestBody E entity, HttpServletResponse response) throws Exception {
		if (dao.exists(entity.id)) {
			throw new EntityExistsException(entity.id);
		}

		try {
			entity.fillWithDefaultValues();
			// todo: use validation.
			entity.createdByUserId = new ObjectId("000000000000000000000000");
			entity.modifiedByUserId = new ObjectId("000000000000000000000000");
			dao.save(entity);
		} catch (ValidationException ex) {
			throw new EntityInvalidException(entity.id, ex.reasons);
		}

		response.setHeader("Location", String.format("/%s/%s", entity.getClass().getSimpleName().toLowerCase(), entity.id));
		return entity;
	}

	@RequestMapping(value = "/{idString:[\\da-fA-F]{24}(?:\\.json)?}", method = RequestMethod.GET)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public E read(@PathVariable String idString) {
		int extensionIndex = idString.lastIndexOf(".json");
		boolean isJson = extensionIndex > 0;
		if (isJson) {
			idString = idString.substring(0, extensionIndex);
		}

		if (!ObjectId.isValid(idString)) {
			throw new InvalidObjectIdException(idString);
		}

		ObjectId objectId = new ObjectId(idString);
		E entity = dao.get(objectId);

		if (entity == null) {
			throw new EntityNotFoundException(objectId);
		}

		return entity;
	}

	@RequestMapping(value = "/{idString:[\\da-fA-F]{24}(?:\\.json)?}", method = RequestMethod.PUT)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	// fixme: get ObjectId from Spring MVC, and let the global validator do the validation.
	// review: is the parameter 'id' necessary?
	public E update(@Valid @PathVariable String idString, @Valid @RequestBody E entity) {
		int extensionIndex = idString.lastIndexOf(".json");
		boolean isJson = extensionIndex > 0;
		if (isJson) {
			idString = idString.substring(0, extensionIndex);
		}

		if (!ObjectId.isValid(idString)) {
			throw new InvalidObjectIdException(idString);
		}

		ObjectId objectId = new ObjectId(idString);

		// todo: check consistency of given id and entity id.
		E oldEntity = dao.get(objectId);

		if (oldEntity == null) {
			throw new EntityNotFoundException(objectId);
		}

		entity.modifiedDate = new Date();

		try {
			// todo: use validation.
			entity.id = oldEntity.id;
			entity.createdByUserId = oldEntity.createdByUserId;
			entity.createdDate = oldEntity.createdDate;
			entity.modifiedByUserId = new ObjectId("000000000000000000000000");
			entity.modifiedDate = new Date();
			dao.save(entity);
		} catch (ValidationException ex) {
			for (int i = 0; i < ex.reasons.size(); i++) {
				logger.error(ex.reasons.get(i));
			}
			throw new EntityInvalidException(objectId, ex.reasons);
		}

		return entity;
	}

	@RequestMapping(value = "/{idString:[\\da-fA-F]{24}(?:\\.json)?}", method = RequestMethod.DELETE)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	// fixme: get ObjectId from Spring MVC, and let the global validator do the validation.
	public E delete(@PathVariable String idString) {
		int extensionIndex = idString.lastIndexOf(".json");
		boolean isJson = extensionIndex > 0;
		if (isJson) {
			idString = idString.substring(0, extensionIndex);
		}

		if (!ObjectId.isValid(idString)) {
			throw new InvalidObjectIdException(idString);
		}

		ObjectId objectId = new ObjectId(idString);
		E entity = dao.deleteById(objectId);

		if (entity == null) {
			throw new EntityNotFoundException(objectId);
		}

		return entity;
	}
}
