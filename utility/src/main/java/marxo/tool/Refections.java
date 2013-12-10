package marxo.tool;

/**
 * <a href="http://stackoverflow.com/questions/442747/getting-the-name-of-the-current-executing-method">Getting the name of the current executing method</a>
 */
public class Refections {
	public static String getMethodName(final int depth) {
		final StackTraceElement[] ste = Thread.currentThread().getStackTrace();

		//System. out.println(ste[ste.length-depth].getClassName()+"#"+ste[ste.length-depth].getMethodName());
		// return ste[ste.length - depth].getMethodName();  //Wrong, fails for depth = 0
		return ste[ste.length - 1 - depth].getMethodName(); //Thank you Tom Tresansky
	}
}
