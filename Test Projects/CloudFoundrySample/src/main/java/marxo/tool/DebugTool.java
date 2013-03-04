package marxo.tool;

public class DebugTool {
	Boolean isDebug = null;

	public boolean isDebug() {
		if (isDebug == null) {
			isDebug = java.lang.management.ManagementFactory.getRuntimeMXBean().getInputArguments().toString().indexOf("-agentlib:jdwp") > 0;
		}

		return isDebug.booleanValue();
	}
}
