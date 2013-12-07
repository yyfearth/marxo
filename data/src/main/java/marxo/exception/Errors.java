package marxo.exception;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties("empty")
public class Errors {
	@JsonUnwrapped
	List<String> messages = new ArrayList<>();

	public void add(String message) {
		messages.add(message);
	}

	public List<String> getMessages() {
		return Lists.newArrayList(messages);
	}

	public boolean isEmpty() {
		return messages.isEmpty();
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < messages.size(); i++) {
			stringBuilder.append(String.format("Err[%d]: %s", i, messages.get(i)));
		}
		return stringBuilder.toString();
	}
}
