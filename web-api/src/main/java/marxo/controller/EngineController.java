package marxo.controller;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import marxo.engine.EngineWorker;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("engine")
public class EngineController extends BasicController {

	@RequestMapping(value = "status", method = RequestMethod.GET)
	public ResponseEntity<Status> getStatus() {
		return new ResponseEntity<>(new Status(EngineWorker.isAlive()), HttpStatus.OK);
	}

	@RequestMapping(value = "start", method = RequestMethod.PUT)
	public ResponseEntity start() {
		EngineWorker.startAsync();
		return new ResponseEntity<Response>(new Response(), HttpStatus.OK);
	}

	@RequestMapping(value = "stop", method = RequestMethod.PUT)
	public ResponseEntity stop() {
		EngineWorker.stop();
		return new ResponseEntity<Response>(new Response(), HttpStatus.OK);
	}

	@JsonSerialize
	public static class Response {

	}

	@JsonSerialize
	public static class Status {
		public boolean isAlive;

		public Status() {
		}

		public Status(boolean alive) {
			isAlive = alive;
		}
	}
}
