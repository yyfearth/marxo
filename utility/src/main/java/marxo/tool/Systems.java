package marxo.tool;

/**
 * From <a href="http://www.mkyong.com/java/how-to-detect-os-in-java-systemgetpropertyosname/">Mkyong</a>
 */
public class Systems {
	public static final String OS_NAME = System.getProperty("os.name");
	public static final String LOWERCASE_OS_NAME = OS_NAME.toLowerCase();

	public static boolean isWindows() {
		return LOWERCASE_OS_NAME.contains("win");
	}

	public static boolean isMac() {
		return LOWERCASE_OS_NAME.contains("mac");
	}

	public static boolean isUnix() {
		return (LOWERCASE_OS_NAME.contains("nix") || LOWERCASE_OS_NAME.contains("nux") || LOWERCASE_OS_NAME.indexOf("aix") > 0);
	}

	public static boolean isSolaris() {
		return LOWERCASE_OS_NAME.contains("sunos");
	}
}
