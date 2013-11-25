package marxo.validation;

import com.google.common.base.Function;
import marxo.entity.BasicEntity;
import org.bson.types.ObjectId;

public class SelectIdFunction implements Function<BasicEntity, ObjectId> {
	protected final static SelectIdFunction selectIdFunction = new SelectIdFunction();

	protected SelectIdFunction() {
	}

	public static SelectIdFunction getInstance() {
		return selectIdFunction;
	}

	@Override
	public ObjectId apply(BasicEntity input) {
		return input.id;
	}
}
