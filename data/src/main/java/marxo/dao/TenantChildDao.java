package marxo.dao;

import marxo.entity.TenantChildEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public abstract class TenantChildDao<E extends TenantChildEntity> extends BasicDao<E> {
	protected ObjectId tenantId;

	public TenantChildDao() {
		this(null);
	}

	public TenantChildDao(ObjectId tenantId) {
		setTenantId(tenantId);
	}

	public ObjectId getTenantId() {
		return new ObjectId(tenantId.toByteArray());
	}

	public void setTenantId(ObjectId tenantId) {
		this.tenantId = tenantId;
	}

	public Criteria getFilterCriteria() {
		return Criteria.where("tenantId").is(tenantId);
	}

	@Override
	public void insert(E entity) {
		entity.tenantId = tenantId;
		super.insert(entity);
	}

	@Override
	public void insert(List<E> entities) {
		for (E workflow : entities) {
			workflow.tenantId = tenantId;
		}
		super.insert(entities);
	}

	@Override
	public void save(E entity) {
		entity.tenantId = tenantId;
		super.save(entity);
	}
}
