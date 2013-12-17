package marxo.controller;

import marxo.entity.action.Content;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("content{:s?}")
public class ContentController extends EntityController<Content> {
}
