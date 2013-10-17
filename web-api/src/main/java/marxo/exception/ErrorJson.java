package marxo.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@JsonSerialize
public class ErrorJson {
	@JsonProperty("errors")
	public List<String> messages = new ArrayList<>();

	public ErrorJson(String... messages) {
		this.messages = Arrays.asList(messages);
	}
}
