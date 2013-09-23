package marxo.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public class ErrorJson {
	@JsonProperty("errors")
	String[] messages;

	public ErrorJson(String... messages) {
		this.messages = messages;
	}
}
