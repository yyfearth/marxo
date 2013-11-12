package marxo.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Trigger extends Context {
	@JsonProperty("is_triggered")
	public boolean isTriggered = false;
}
