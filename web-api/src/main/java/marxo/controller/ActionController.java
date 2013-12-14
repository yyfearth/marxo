package marxo.controller;

import marxo.entity.action.Action;
import marxo.exception.EntityNotFoundException;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("action{:s?}")
public class ActionController extends EntityController<Action> {
	@Override
	public Action read(@PathVariable String idString) throws Exception {
		ObjectId objectId = stringToObjectId(idString);

		Criteria criteria = Criteria
				.where("tenantId").is(user.tenantId)
				.and("_id").is(objectId);
		Action action = mongoTemplate.findOne(Query.query(criteria), entityClass);

		if (action == null) {
			throw new EntityNotFoundException(entityClass, objectId);
		}

		return action;
	}
}
