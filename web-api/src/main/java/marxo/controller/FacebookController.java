package marxo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

//@Controller
@RequestMapping("/service/facebook")
public class FacebookController extends BasicController {
	@RequestMapping
	@ResponseBody
	public String test() {
		return "Hello world";
	}
}
