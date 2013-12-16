package marxo.controller;

import com.mongodb.WriteResult;
import marxo.entity.BasicEntity;
import marxo.entity.MongoDbAware;
import marxo.entity.user.User;
import marxo.exception.*;
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
	protected Class<Entity> entityClass;
	// review: not sure storing a query is a better idea.
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
	}

	protected Criteria getIdCriteria(ObjectId objectId) {
		return Criteria.where("_id").is(objectId);
	}

	protected void throwIfError(WriteResult writeResult) {
		if (writeResult.getError() != null) {
			logger.debug(String.format("Database error: %s", writeResult.getError()));
			throw new DatabaseException(writeResult.getError());
		}
	}

	protected Sort getDefaultSort() {
		return new Sort(new Sort.Order(Sort.Direction.DESC, "updateTime")).and(new Sort(new Sort.Order(Sort.Direction.DESC, "createTime")));
	}

	protected Criteria newDefaultCriteria() {
		return new Criteria();
	}

	protected Criteria newDefaultCriteria(ObjectId id) {
		return Criteria.where("_id").is(id);
	}

	protected Query newDefaultQuery(ObjectId id) {
		return Query.query(newDefaultCriteria(id));
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
		ObjectId objectId = stringToObjectId(idString);

		Entity entity = mongoTemplate.findOne(newDefaultQuery(objectId), entityClass);

		if (entity == null) {
			throw new EntityNotFoundException(entityClass, objectId);
		}

		return entity;
	}

	@RequestMapping(value = "/{idString:[\\da-fA-F]{24}}", method = RequestMethod.PUT)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public Entity update(@Valid @PathVariable String idString, @Valid @RequestBody Entity entity) throws Exception {
		ObjectId objectId = stringToObjectId(idString);

		Entity oldEntity = mongoTemplate.findOne(newDefaultQuery(objectId), entityClass);

		if (oldEntity == null) {
			throw new EntityNotFoundException(entityClass, objectId);
		}

		try {
			entity.id = oldEntity.id;
			entity.createUserId = oldEntity.createUserId;
			entity.createTime = oldEntity.createTime;
			entity.updateUserId = user.id;
			entity.updateTime = DateTime.now();
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
		ObjectId objectId = stringToObjectId(idString);

		Entity entity = mongoTemplate.findOne(newDefaultQuery(objectId), entityClass);
		if (entity == null) {
			throw new EntityNotFoundException(entityClass, objectId);
		}

		entity.remove();

		return entity;
	}

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public List<Entity> search() {
		return mongoTemplate.find(new Query(newDefaultCriteria()).with(getDefaultSort()), entityClass);
	}
}
