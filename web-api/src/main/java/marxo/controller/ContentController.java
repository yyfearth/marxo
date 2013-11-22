package marxo.controller;

import marxo.entity.content.Content;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("content{:s?}")
public class ContentController extends EntityController<Content> {
}
