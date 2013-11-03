package marxo.controller;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import marxo.exception.ErrorJson;
import marxo.tool.AdvancedGenerator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
@RequestMapping("/")
public class TestController extends BasicController {
	@RequestMapping
	@ResponseBody
	public RemoteInfomation get(HttpServletRequest request) {
		return new RemoteInfomation(request);
	}

	@RequestMapping("error404")
	@ResponseBody
	public ResponseEntity<ErrorJson> handleError404(HttpServletRequest request, HttpServletResponse response) {
		String requestedUri = (String) request.getAttribute("javax.servlet.error.request_uri");
		ErrorJson errorJson = new ErrorJson("The path (" + requestedUri + ") cannot be found");
		return new ResponseEntity<>(errorJson, HttpStatus.NOT_FOUND);
	}

	@RequestMapping("/test{:s?}")
	@ResponseBody
	public List<Double> getRandomNumbers() {
		List<Double> list = new ArrayList<>();

		for (int i = 0; i < 10; i++) {
			list.add(Math.random());
		}

		return list;
	}

	@RequestMapping("/reset")
	@ResponseBody
	public String resetDatabase() {
		AdvancedGenerator.main(new String[0]);
		return "Database reset with " + AdvancedGenerator.class.getSimpleName();
	}

	@RequestMapping("/logout")
	@ResponseBody
	public ResponseEntity<String> logout() {
		return new ResponseEntity<String>("You are signed out", HttpStatus.UNAUTHORIZED);
	}
}

@JsonSerialize
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
class RemoteInfomation {
	protected String ip;
	protected String host;
	protected int port;
	protected Map<String, String> headers;

	public RemoteInfomation(HttpServletRequest request) {
		ip = request.getRemoteAddr();
		host = request.getRemoteHost();
		port = request.getRemotePort();

		headers = new HashMap<>();
		List<String> headerNames = Collections.list(request.getHeaderNames());
		for (String headerName : headerNames) {
			headers.put(headerName, request.getHeader(headerName));
		}
	}
}
