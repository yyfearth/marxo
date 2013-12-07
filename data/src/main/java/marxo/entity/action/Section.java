package marxo.entity.action;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

public class Section {

	public static enum Type {
		NONE,
		FILE,
		IMAGE,
		TEXT,
		TEXTAREA,
		HTML,
		RADIO,
	}

	public String name;
	@JsonProperty("desc")
	public String description;
	public Type type = Type.NONE;
	public Map<String, Object> options = new HashMap<>();
}
