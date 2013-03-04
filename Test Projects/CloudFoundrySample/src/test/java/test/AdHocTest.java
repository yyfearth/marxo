package test;

import org.testng.annotations.Test;

/**
 * Temporary playground. Please have fun.
 */
public class AdHocTest {
	@Test
	public void test() {
		boolean isDebug = java.lang.management.ManagementFactory.getRuntimeMXBean().
				getInputArguments().toString().indexOf("-agentlib:jdwp") > 0;

		System.out.println(isDebug);
	}
}
