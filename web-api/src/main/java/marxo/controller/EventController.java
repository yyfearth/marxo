package marxo.controller;

import marxo.entity.node.Event;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("event{:s?}")
public class EventController extends EntityController<Event> {
	protected EventController() {
		super();
	}

	@Override
	public void preHandle() {
		super.preHandle();
	}

	@Override
	protected Query getDefaultQuery(Criteria criteria) {
		return super.getDefaultQuery(criteria);
	}

	@Override
	public Event create(@Valid @RequestBody Event entity, HttpServletResponse response) throws Exception {
		return super.create(entity, response);
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
