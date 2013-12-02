package marxo.entity;

import marxo.entity.action.ActionChildEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "report")
public class Report extends ActionChildEntity {

	public static Report get(ObjectId objectId) {
		return mongoTemplate.findById(objectId, Report.class);
	}
}
