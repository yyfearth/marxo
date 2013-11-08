package marxo.dao;

import com.google.common.base.Strings;
import com.mongodb.WriteResult;
import marxo.entity.FacebookData;
import marxo.entity.Tenant;
import marxo.exception.DatabaseException;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@Repository
public class TenantDao extends BasicDao<Tenant> {
	public FacebookData updateFacebookData(ObjectId tenantId, FacebookData data) {
		Query query = Query.query(Criteria.where("id").is(tenantId));
		Update update = Update.update("facebookData", data);
		WriteResult writeResult = mongoTemplate.updateFirst(query, update, Tenant.class);
		if (!Strings.isNullOrEmpty(writeResult.getError())) {
			throw new DatabaseException(String.format("[Query] %s [Update] %s [Error] %s", query.toString(), update.toString(), writeResult.getError()));
		}

		return data;
	}

	public void removeFacebookData(ObjectId tenantId) {
		Query query = Query.query(Criteria.where("id").is(tenantId));
		Update update = Update.update("facebookData", null);
		WriteResult writeResult = mongoTemplate.updateFirst(query, update, Tenant.class);
		if (!Strings.isNullOrEmpty(writeResult.getError())) {
			throw new DatabaseException(String.format("[Query] %s [Update] %s [Error] %s", query.toString(), update.toString(), writeResult.getError()));
		}
	}
}
