package marxo.controller;

import marxo.entity.node.Event;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("event{:s?}")
public class EventController extends EntityController<Event> {
}
