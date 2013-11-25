package marxo.controller;

import marxo.entity.node.Event;
import org.joda.time.DateTime;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("event{:s?}")
public class EventController extends EntityController<Event> {
	@Override
	public void preHandle() {
		super.preHandle();
	}

	@Override
	public Event create(@Valid @RequestBody Event entity, HttpServletResponse response) throws Exception {
//		Criteria criteria = Criteria.where()
//		Node node = mongoTemplate.findById(entity.ac)

		entity.createUserId = entity.updateUserId = user.id;
		entity.createTime = entity.updateTime = DateTime.now();

		mongoTemplate.save(entity);

		response.setHeader("Location", String.format("/%s/%s", entity.getClass().getSimpleName().toLowerCase(), entity.id));
		return entity;
	}

	@Override
	public Event read(@PathVariable String idString) throws Exception {
		return super.read(idString);
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
