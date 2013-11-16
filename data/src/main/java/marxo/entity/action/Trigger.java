package marxo.entity.action;

import com.fasterxml.jackson.annotation.JsonProperty;
import marxo.entity.action.Context;

public class Trigger extends Context {
	@JsonProperty("is_triggered")
	public boolean isTriggered = false;
}
