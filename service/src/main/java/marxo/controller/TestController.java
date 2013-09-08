package marxo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/test{:s?}")
public class TestController {
	@RequestMapping
	@ResponseBody
	public List<Integer> getWorkflow() {
		List<Integer> list = new ArrayList<>(10);

		for (int i = 0; i < 10; i++) {
			list.add(i);
		}

		return list;
	}

	@RequestMapping("/1")
	@ResponseBody
	public int[] get1() {
		int[] a = new int[10];

		for (int i = 0; i < a.length; i++) {
			a[i] = i + 1;
		}

		return a;
	}
}
