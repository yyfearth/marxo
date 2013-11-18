package marxo.entity.workflow;

import com.google.common.base.Predicate;
import com.sun.istack.internal.Nullable;
import marxo.entity.workflow.WorkflowChildEntity;
import org.bson.types.ObjectId;

public class WorkflowPredicate<E extends WorkflowChildEntity> implements Predicate<E> {
	ObjectId workflowId;

	public WorkflowPredicate(ObjectId workflowId) {
		this.workflowId = workflowId;
	}

	@Override
	public boolean apply(@Nullable E entity) {
		return (entity != null) && workflowId.equals(entity.workflowId);
	}
}