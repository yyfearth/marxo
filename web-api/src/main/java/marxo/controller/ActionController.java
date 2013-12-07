package marxo.controller;

import marxo.entity.action.Action;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("action{:s?}")
public class ActionController extends EntityController<Action> {
}
