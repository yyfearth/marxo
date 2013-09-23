package marxo.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public class ErrorJson {
	String errorMessage = "I forget to put message for this error :P";

	public ErrorJson(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	@JsonProperty("error")
	public String getErrorMessage() {
		return errorMessage;
	}
}
