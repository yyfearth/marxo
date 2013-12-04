package marxo.entity.content;

import marxo.entity.action.ActionChildEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "content")
public class Content extends ActionChildEntity {

	public String getType() {
		String typeWithoutAction = getClass().getSimpleName().replace("Content", "");
		String typeWithUnderscores = typeWithoutAction.replaceAll("([^$])([A-Z])", "$1_$2");
		return typeWithUnderscores.toUpperCase();
	}

	/*
	DAO
	 */

	public static Content get(ObjectId id) {
		return mongoTemplate.findById(id, Content.class);
	}
}
