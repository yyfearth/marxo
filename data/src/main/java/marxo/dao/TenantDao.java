package marxo.dao;

import marxo.entity.Tenant;
import org.springframework.stereotype.Repository;

@Repository("tenantDao")
public class TenantDao extends BasicDao<Tenant> {
}
