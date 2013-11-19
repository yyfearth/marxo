package marxo.dao;

import marxo.entity.workflow.WorkflowChildEntity;
import org.bson.types.ObjectId;

import java.util.List;

public abstract class WorkflowChildDao<Entity extends WorkflowChildEntity> extends TenantChildDao<Entity> {
}
