package marxo.controller;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping
public class TestController {
	final Logger logger = LoggerFactory.getLogger(TestController.class);

	@PostConstruct
	void report() {
		logger.info("TestController started");
	}

	@RequestMapping
	@ResponseBody
	public Message get() {
		return new Message("Hello world");
	}
//	public List<Integer> get() {
//		ArrayList<Integer> list = new ArrayList<>();
//
//		for (int i = 0; i < 5; i++) {
//			list.add(i);
//		}
//
//		return list;
//	}

	@RequestMapping("/test{:s?}")
	@ResponseBody
	public List<Integer> getWorkflow() {
		List<Integer> list = new ArrayList<>(10);

		for (int i = 0; i < 10; i++) {
			list.add(i);
		}

		return list;
	}

	@JsonSerialize
	class Message {
		String message = "";

		public Message(String message) {
			this.message = message;
		}
	}
}
