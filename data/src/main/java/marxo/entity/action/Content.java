package marxo.entity.action;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.restfb.types.Comment;
import com.restfb.types.FacebookType;
import marxo.entity.report.Record;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "content")
public class Content extends ActionChildEntity {

	public Content() {
	}

	public Content(Type type) {
		setType(type);
	}

	/*
	Type
	 */

	public static enum Type {
		DEFAULT,
		FACEBOOK,
		TWITTER,
		EMAIL,
		PAGE,
	}

	protected Type type = Type.DEFAULT;

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;

		switch (type) {
			case DEFAULT:
				break;
			case FACEBOOK:
				records = new ArrayList<>();
				comments = new ArrayList<>();

				break;
			case TWITTER:
				break;
			case EMAIL:
				break;
			case PAGE:
				records = new ArrayList<>();
				submissions = new ArrayList<>();
				break;
		}
	}

	/*
	Facebook
	 */

	public String message;
	public FacebookType messageResponse;

	public String getPostId() {
		if (messageResponse == null) {
			return null;
		}
		return messageResponse.getId();
	}

	public List<Record> records;
	public List<Comment> comments;

	/*
	Page
	 */

	@JsonProperty("posted_at")
	protected DateTime postedTime;

	public DateTime getPostedTime() {
		return postedTime;
	}

	public void setPostedTime(DateTime postedTime) {
		this.postedTime = postedTime;
	}

	public String parsedMessage;
	public List<Submission> submissions;
	public List<Section> sections;

	/*
	View count.
	 */

	protected int viewCount = 0;

	public int getViewCount() {
		return viewCount;
	}

	public void setViewCount(int viewCount) {
		this.viewCount = viewCount;
	}

	public void increaseViewCount() {
		viewCount++;
	}

	/*
	DAO
	 */

	public static Content get(ObjectId id) {
		return mongoTemplate.findById(id, Content.class);
	}

	public static void remove(ObjectId objectId) {
		mongoTemplate.findAndRemove(Query.query(Criteria.where("_id").is(objectId)), Content.class);
	}
}
