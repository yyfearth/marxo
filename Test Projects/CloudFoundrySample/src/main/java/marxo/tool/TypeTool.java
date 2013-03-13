package marxo.tool;

import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class TypeTool {
	/**
	 * Convert a list of object to another list of objects which is the type of base class of the original.
	 * Fuck Java and its type erasure!
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

	public static <T> Class<T> getType(T obj) {
		return (Class<T>) obj.getClass();
	}

	public static ObjectId[] stringsToObjectIds(String[] strings) {
		ObjectId[] objectIds = new ObjectId[strings.length];

		for (int i = 0; i < strings.length; i++) {
			objectIds[i] = new ObjectId(strings[i]);
		}

		return objectIds;
	}

	public static String[] objectIdsToStrings(ObjectId[] objectIds) {
		String[] strings = new String[objectIds.length];

		for (int i = 0; i < objectIds.length; i++) {
			strings[i] = objectIds[i].toString();
		}

		return strings;
	}
}
