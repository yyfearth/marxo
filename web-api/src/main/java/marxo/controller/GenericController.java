package marxo.controller;

import marxo.bean.Entity;
import marxo.dao.BasicDao;
import marxo.exception.*;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;

public abstract class GenericController<E extends Entity, Dao extends BasicDao<E>> extends BasicController {
	Dao dao;

	protected GenericController(Dao dao) {
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
			dao.save(entity);
		} catch (ValidationException ex) {
			throw new EntityInvalidException(entity.id, ex.reasons);
		}

		response.setHeader("Location", String.format("/%s/%s", entity.getClass().getSimpleName().toLowerCase(), entity.id));
		return entity;
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public E read(@PathVariable String id) {
		if (!ObjectId.isValid(id)) {
			throw new InvalidObjectIdException(id);
		}

		ObjectId objectId = new ObjectId(id);
		E entity = dao.get(objectId);

		if (entity == null) {
			throw new EntityNotFoundException(objectId);
		}

		return entity;
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	// fixme: get ObjectId from Spring MVC, and let the global validator do the validation.
	// review: is the parameter 'id' necessary?
	public E update(@Valid @PathVariable String id, @Valid @RequestBody E entity) {
		if (!ObjectId.isValid(id)) {
			throw new InvalidObjectIdException(id);
		}

		ObjectId objectId = new ObjectId(id);

		// todo: check consistency of given id and entity id.
		E oldEntity = dao.get(objectId);

		if (oldEntity == null) {
			throw new EntityNotFoundException(objectId);
		}

		entity.modifiedDate = new Date();

		try {
			dao.save(entity);
		} catch (ValidationException ex) {
			for (int i = 0; i < ex.reasons.size(); i++) {
				logger.error(ex.reasons.get(i));
			}
			throw new EntityInvalidException(objectId, ex.reasons);
		}

		return entity;
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	// fixme: get ObjectId from Spring MVC, and let the global validator do the validation.
	public E delete(@PathVariable String id) {
		if (!ObjectId.isValid(id)) {
			throw new InvalidObjectIdException(id);
		}

		ObjectId objectId = new ObjectId(id);
		E entity = dao.deleteById(objectId);

		if (entity == null) {
			throw new EntityNotFoundException(objectId);
		}

		return entity;
	}
}
