package marxo.controller;

import marxo.entity.node.Action;
import marxo.entity.node.Event;
import marxo.entity.node.Node;
import marxo.exception.EntityNotFoundException;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("event{:s?}")
public class EventController extends EntityController<Event> {

	@Override
	public Event create(@Valid @RequestBody Event entity, HttpServletResponse response) throws Exception {
		Assert.notNull(entity);
		Assert.notNull(entity.actionId);

		entity.createUserId = entity.updateUserId = user.id;
		entity.createTime = entity.updateTime = DateTime.now();

		mongoTemplate.save(entity);

		response.setHeader("Location", String.format("/%s/%s", entity.getClass().getSimpleName().toLowerCase(), entity.id));
		return entity;
	}

	@Override
	public Event read(@PathVariable String idString) throws Exception {
		Assert.isTrue(ObjectId.isValid(idString));

		ObjectId eventId = new ObjectId(idString);
		Criteria criteria = Criteria.where("actions.event._id").is(eventId);
		Query query = Query.query(criteria);
		Node node = mongoTemplate.findOne(query, Node.class);
		node.wire();

		for (Action action : node.getActions()) {
			Event event = action.getEvent();
			if (event.id.equals(eventId)) {
				return event;
			}
		}

		throw new EntityNotFoundException(Event.class, eventId);
	}

	@Override
	public Event update(@Valid @PathVariable String idString, @Valid @RequestBody Event entity) throws Exception {
		return super.update(idString, entity);
	}

	@Override
	public Event delete(@PathVariable String idString) throws Exception {
		return super.delete(idString);
	}

	@Override
	public List<Event> search() {
		return super.search();
	}
}
