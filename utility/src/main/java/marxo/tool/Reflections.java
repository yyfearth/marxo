package marxo.tool;

import com.sun.istack.internal.NotNull;

import java.lang.reflect.Field;

public class Reflections {

	/**
	 * <a href="http://stackoverflow.com/questions/442747/getting-the-name-of-the-current-executing-method">Getting the name of the current executing method</a>
	 */
	public static String getMethodName(final int depth) {
		final StackTraceElement[] ste = Thread.currentThread().getStackTrace();

		return ste[ste.length - 1 - depth].getMethodName();
	}

	public static String getObjectDump(@NotNull Object object) {
		Class<?> aClass = object.getClass();
		StringBuilder stringBuilder = new StringBuilder(aClass.getSimpleName() + "\n");

		Field[] fields = aClass.getFields();
		for (Field field : fields) {
			field.setAccessible(true);
			try {
				stringBuilder.append("\t" + field.getName() + ": " + field.get(object) + "\n");
			} catch (IllegalAccessException e) {
				stringBuilder.append("\t" + field.getName() + " ?\n");
				continue;
			}
		}

		return stringBuilder.toString();
	}
}
