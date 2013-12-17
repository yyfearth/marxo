package marxo.tool;

public class NumberTool {
	public static Integer parse(String s) {
		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	public static int valueOrDefault(Integer value, int defaultValue) {
		return (value == null) ? defaultValue : value;
	}

	public static int valueOrDefault(Integer value) {
		return valueOrDefault(value, 0);
	}
}
