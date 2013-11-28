package marxo.validation;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

public class Errors {
	List<String> messages = new ArrayList<>();

	public void add(String message) {
		messages.add(message);
	}

	public List<String> getMessages() {
		return ImmutableList.copyOf(messages);
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
