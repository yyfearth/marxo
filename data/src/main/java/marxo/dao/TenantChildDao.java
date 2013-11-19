package marxo.dao;

import marxo.entity.user.TenantChildEntity;
import org.bson.types.ObjectId;

import java.util.List;

public abstract class TenantChildDao<Entity extends TenantChildEntity> extends BasicDao<Entity> {
}
