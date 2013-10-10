package marxo.tool;

public class StringTool {
	public final static String regexEscapeString = "([\\$\\^\\[\\]\\{\\}\\(\\)\\.\\|\\+\\*\\?\\\\])";

	private StringTool() {
	}

	public static String escapePatternCharacters(String patternString) {
		return patternString.replaceAll(regexEscapeString, "\\\\$1");
	}
}
