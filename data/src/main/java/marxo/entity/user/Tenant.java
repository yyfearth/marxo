package marxo.entity.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import marxo.entity.BasicEntity;
import marxo.entity.FacebookData;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class Tenant extends BasicEntity {
	public String contact;
	public String email;
	@JsonProperty("tel")
	public String phoneNumber;
	public String fax;
	public String address;
	public FacebookData facebookData;

	public static void removeAll() {
		mongoTemplate.dropCollection(Tenant.class);
	}

	public static Tenant get(ObjectId id) {
		return mongoTemplate.findOne(Query.query(Criteria.where("_id").is(id)), Tenant.class);
	}
}
