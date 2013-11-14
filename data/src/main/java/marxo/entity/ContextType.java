package marxo.entity;

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
