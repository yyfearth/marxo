package marxo.controller;

import marxo.entity.BasicEntity;
import marxo.entity.MongoDbAware;
import marxo.entity.user.User;
import marxo.exception.EntityInvalidException;
import marxo.exception.EntityNotFoundException;
import marxo.exception.InvalidObjectIdException;
import marxo.exception.ValidationException;
import marxo.security.MarxoAuthentication;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.List;

@SuppressWarnings("unchecked")
public abstract class EntityController<Entity extends BasicEntity> extends BasicController implements MongoDbAware, InterceptorPreHandlable {
	protected static Sort defaultSort = new Sort(new Sort.Order(Sort.Direction.DESC, "updateTime")).and(new Sort(new Sort.Order(Sort.Direction.DESC, "createTime")));
	protected Class<Entity> entityClass;
	// review: not sure storing a query is a better idea.
	protected Criteria criteria;
	/**
	 * The user who is using the controller.
	 */
	protected User user;
	@Autowired
	HttpServletRequest request;

	protected EntityController() {
		Class<?> targetClass = getClass();
		Type type = targetClass.getGenericSuperclass();
		while (!(type instanceof ParameterizedType)) {
			targetClass = getClass().getSuperclass();
			type = targetClass.getGenericSuperclass();
		}

		ParameterizedType parameterizedType = (ParameterizedType) type;
		this.entityClass = (Class<Entity>) parameterizedType.getActualTypeArguments()[0];
	}

	@Override
	public void preHandle() {
		MarxoAuthentication marxoAuthentication = (MarxoAuthentication) SecurityContextHolder.getContext().getAuthentication();
		Assert.notNull(marxoAuthentication);
		user = marxoAuthentication.getUser();
		criteria = new Criteria();
	}

	protected Query getDefaultQuery(Criteria criteria) {
		return Query.query(criteria).with(defaultSort);
	}

	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(HttpStatus.CREATED)
	public Entity create(@Valid @RequestBody Entity entity, HttpServletResponse response) throws Exception {
		entity.createUserId = entity.updateUserId = user.id;
		entity.createTime = entity.updateTime = DateTime.now();
		entity.save();

		URI location = ServletUriComponentsBuilder.fromServletMapping(request).path("/{entityName}/{id}").build().expand(entity.getClass().getSimpleName().toLowerCase(), entity.id).toUri();
		response.setHeader("Localtion", location.toString());
		return entity;
	}

	@RequestMapping(value = "/{idString:[\\da-fA-F]{24}}", method = RequestMethod.GET)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public Entity read(@PathVariable String idString) throws Exception {
		if (!ObjectId.isValid(idString)) {
			throw new InvalidObjectIdException(idString);
		}

		ObjectId objectId = new ObjectId(idString);
		criteria.and("id").is(objectId);
		Entity entity = mongoTemplate.findOne(getDefaultQuery(criteria), entityClass);

		if (entity == null) {
			throw new EntityNotFoundException(entityClass, objectId);
		}

		return entity;
	}

	@RequestMapping(value = "/{idString:[\\da-fA-F]{24}}", method = RequestMethod.PUT)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public Entity update(@Valid @PathVariable String idString, @Valid @RequestBody Entity entity) throws Exception {
		if (!ObjectId.isValid(idString)) {
			throw new InvalidObjectIdException(idString);
		}

		ObjectId objectId = new ObjectId(idString);
		assert objectId.equals(entity.id);

		Entity oldEntity = mongoTemplate.findById(objectId, entityClass);

		if (oldEntity == null) {
			throw new EntityNotFoundException(entityClass, objectId);
		}

		try {
			entity.id = oldEntity.id;
			entity.createUserId = oldEntity.createUserId;
			entity.createTime = oldEntity.createTime;
			entity.updateUserId = user.id;
			entity.save();
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
	public Entity delete(@PathVariable String idString) throws Exception {
		if (!ObjectId.isValid(idString)) {
			throw new InvalidObjectIdException(idString);
		}

		ObjectId objectId = new ObjectId(idString);
		criteria.and("id").is(objectId);
		Entity entity = mongoTemplate.findAndRemove(getDefaultQuery(criteria), entityClass);

		if (entity == null) {
			throw new EntityNotFoundException(entityClass, objectId);
		}

		return entity;
	}

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public List<Entity> search() {
		return mongoTemplate.find(getDefaultQuery(criteria), entityClass);
	}

}
