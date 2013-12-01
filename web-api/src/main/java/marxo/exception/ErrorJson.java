package marxo.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

@JsonSerialize
public class ErrorJson {
	@JsonProperty("errors")
	public List<String> messages = new ArrayList<>();

	public ErrorJson() {
	}

	public ErrorJson(String... messages) {
		this.messages = Lists.newArrayList(messages);
	}
}
