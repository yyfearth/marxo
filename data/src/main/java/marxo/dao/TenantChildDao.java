package marxo.dao;

import marxo.entity.user.TenantChildEntity;
import org.bson.types.ObjectId;

import java.util.List;

public abstract class TenantChildDao<Entity extends TenantChildEntity> extends BasicDao<Entity> {
	final public ObjectId tenantId;

	public TenantChildDao(ObjectId tenantId) {
		this.tenantId = tenantId;
	}

	@Override
	protected void processEntity(Entity entity) {
		if (tenantId != null) {
			entity.tenantId = tenantId;
		}
	}

	@Override
	protected void processDataPairs(List<DataPair> dataPairs) {
		if (tenantId != null) {
			dataPairs.add(new DataPair("tenantId", tenantId));
		}
	}
}
