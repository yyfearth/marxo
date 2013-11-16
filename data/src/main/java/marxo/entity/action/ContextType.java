package marxo.entity.action;

import marxo.entity.Schedule;
import marxo.entity.action.Context;
import marxo.entity.action.FacebookPost;
import marxo.entity.action.Trigger;

public enum ContextType {
	CREATE_PAGE(FacebookPost.class),
	POST_FACEBOOK(FacebookPost.class),
	SCHEDULE_EVENT(Schedule.class),
	TRIGGER(Trigger.class),
	GENERATE_REPORT(Trigger.class);
	Class<? extends Context> contextClass;

	private ContextType(Class<? extends Context> contextClass) {
		this.contextClass = contextClass;
	}

	public Class<? extends Context> getContextClass() {
		return contextClass;
	}
}
