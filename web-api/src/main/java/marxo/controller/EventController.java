package marxo.controller;

import marxo.entity.node.Event;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("event{:s?}")
public class EventController extends EntityController<Event> {
}
