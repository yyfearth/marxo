package marxo.validation;

import com.google.common.base.Function;
import com.sun.istack.internal.Nullable;
import marxo.entity.BasicEntity;
import org.bson.types.ObjectId;

public class SelectIdFunction implements Function<BasicEntity, ObjectId> {


	@Override
	public ObjectId apply(@Nullable BasicEntity input) {
		return input.id;
	}
}
