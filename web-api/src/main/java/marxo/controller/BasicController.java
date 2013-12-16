package marxo.controller;

import marxo.exception.InvalidObjectIdException;
import marxo.tool.Loggable;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

public abstract class BasicController implements Loggable {
	@Autowired
	ApplicationContext applicationContext;

	protected ObjectId stringToObjectId(String idString) {
		if (!ObjectId.isValid(idString)) {
			throw new InvalidObjectIdException(idString);
		}
		return new ObjectId(idString);
	}
}
