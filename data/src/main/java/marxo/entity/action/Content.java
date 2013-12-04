package marxo.entity.action;

import com.restfb.types.Comment;
import com.restfb.types.FacebookType;
import com.restfb.types.Post;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "content")
public class Content extends ActionChildEntity {

	public Content() {
	}

	public Content(Type type) {
		this.type = type;
	}

	public static enum Type {
		DEFAULT,
		FACEBOOK,
		TWITTER,
		EMAIL,
		PAGE,
	}

	public Type type = Type.DEFAULT;

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

	public List<Post> posts = new ArrayList<>();

	public static class FacebookRecord {
		public DateTime time;
		public long linkCount = 0;
		public long commentCount = 0;
		public long shareCount = 0;

		public static FacebookRecord fromPost(Post post) {
			FacebookRecord facebookRecord = new FacebookRecord();
			facebookRecord.linkCount = post.getLikesCount();
			facebookRecord.shareCount = post.getSharesCount();
			facebookRecord.commentCount = post.getComments().getCount();
			return facebookRecord;
		}
	}

	public List<FacebookRecord> records = new ArrayList<>();
	public List<Comment> comments = new ArrayList<>();

	/*
	DAO
	 */

	public static Content get(ObjectId id) {
		return mongoTemplate.findById(id, Content.class);
	}
}
