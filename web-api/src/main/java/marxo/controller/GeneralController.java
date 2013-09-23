package marxo.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/")
public class GeneralController {
	final Logger logger = LoggerFactory.getLogger(GeneralController.class);
	@Autowired
	ApplicationContext applicationContext;

	@PostConstruct
	void report() {
		logger.debug(GeneralController.class.getSimpleName() + " started");

		Boolean isDebug = applicationContext.getBean("isDebug", Boolean.class);
		isDebug = (isDebug == null) ? false : isDebug;

		if (isDebug) {
			System.gc();
		}
	}

	@RequestMapping
	@ResponseBody
	public Message get() {
		return new Message("Hello world");
	}

	@RequestMapping("/test{:s?}")
	@ResponseBody
	public List<Double> getWorkflow() {
		List<Double> list = new ArrayList<>();

		for (int i = 0; i < 10; i++) {
			list.add(Math.random());
		}

		return list;
	}

//	@ExceptionHandler({BindException.class})
//	@ResponseStatus(HttpStatus.NOT_FOUND)
//	public ErrorJson handleNotFound() {
//		return new ErrorJson("Your shit is not here");
//	}

	@JsonSerialize
	class Message {
		@JsonProperty
		String message = "";

		public Message(String message) {
			this.message = message;
		}
	}
}
