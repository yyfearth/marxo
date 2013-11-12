package marxo.entity;

public enum ContextType {
	FB_POST(FacebookPost.class),
	SCHEDULE(Schedule.class),
	TRIGGER(Trigger.class);
	Class<? extends Context> contextClass;

	private ContextType(Class<? extends Context> contextClass) {
		this.contextClass = contextClass;
	}

	public Class<? extends Context> getContextClass() {
		return contextClass;
	}
}
