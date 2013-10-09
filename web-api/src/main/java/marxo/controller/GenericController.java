package marxo.controller;

import marxo.bean.Entity;
import marxo.bean.Node;
import marxo.dao.BasicDao;
import marxo.exception.*;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import java.util.List;

@Service
public abstract class GenericController<E extends Entity, Dao extends BasicDao<E>> extends BasicController {
	Dao dao;

	protected GenericController(Dao dao) {
		this.dao = dao;
	}

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public List<E> getAll() {
		return dao.findAll();
	}

	@RequestMapping(value = "/", method = RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(HttpStatus.CREATED)
	public E create(@Valid @RequestBody E entity) throws Exception {
		if (dao.exists(entity.id)) {
			throw new EntityExistsException(entity.id);
		}

		try {
			dao.save(entity);
		} catch (ValidationException ex) {
			// todo: add error message
			throw new EntityInvalidException(entity.id, "not implemented");
		}

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

		try {
			dao.save(oldEntity);
		} catch (ValidationException e) {
			for (int i = 0; i < e.reasons.size(); i++) {
				logger.error(e.reasons.get(i));
			}
			// todo: find a way to generate multiple reason messages to client.
//			throw new EntityInvalidException(objectId, );
		}

		return oldEntity;
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
