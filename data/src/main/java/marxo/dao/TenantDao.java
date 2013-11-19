package marxo.dao;

import marxo.entity.FacebookData;
import marxo.entity.user.Tenant;
import org.springframework.stereotype.Repository;

@Repository
public class TenantDao extends BasicDao<Tenant> {

	public void updateFacebookData(DaoContext context, FacebookData data) {
		updateFirst(context, DaoContext.newInstance().addContext(
				new DaoContextData("facebookData", DaoContextOperator.SET, data)
		));
	}

	public void removeFacebookData(DaoContext context) {
		updateFirst(context, DaoContext.newInstance().addContext(
				new DaoContextData("facebookData", DaoContextOperator.UNSET)
		));
	}
}
