package marxo.tool;

import marxo.bean.BasicEntity;
import org.bson.types.ObjectId;

import java.lang.reflect.Array;
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

	public static <T extends BasicEntity> T[] toEntities(Class<T> targetClass, List<ObjectId> idList) {
		try {
			@SuppressWarnings("unchecked")
			T[] entities = (T[]) Array.newInstance(targetClass, idList.size());

			for (int i = 0; i < entities.length; i++) {
				T entity = targetClass.newInstance();
				entity.setId(idList.get(i));
				entities[i] = entity;
			}

			return entities;
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static List<ObjectId> toIdList(BasicEntity[] entities) {
		List<ObjectId> idList = new ArrayList<ObjectId>();

		for (int i = 0; i < entities.length; i++) {
			idList.add(i, entities[i].getId());
		}

		return idList;
	}
}
