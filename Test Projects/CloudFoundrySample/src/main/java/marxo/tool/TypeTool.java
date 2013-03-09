package marxo.tool;

import java.util.ArrayList;
import java.util.List;

public class TypeTool {
	/**
	 * Convert a list of object to another list of objects which is the type of base class of the original.
	 * Fuck Java!
	 *
	 * @param list the original list
	 * @param <T1> the type being converted from
	 * @param <T2> the type being converted to
	 * @return the converted list
	 */
	public static <T1 extends T2, T2> List<T2> convert(List<T1> list) {
		List<T2> newList = new ArrayList<T2>(list.size());

		for (T1 o : list) {
			newList.add(o);
		}

		return newList;
	}
}
