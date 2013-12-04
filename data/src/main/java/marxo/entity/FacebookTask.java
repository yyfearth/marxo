package marxo.entity;

import marxo.entity.action.PostFacebookAction;
import org.joda.time.DateTime;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.Assert;

@Document(collection = "task")
public class FacebookTask extends BasicEntity {
	public PostFacebookAction postFacebookAction;
	public DateTime time = DateTime.now();

	public FacebookTask(PostFacebookAction postFacebookAction) {
		Assert.notNull(postFacebookAction);
		this.postFacebookAction = postFacebookAction;
	}

	public static FacebookTask next() {
		Criteria criteria = Criteria.where("time").lte(DateTime.now());
		Query query = Query.query(criteria);
		return mongoTemplate.findAndRemove(query, FacebookTask.class);
	}
}
