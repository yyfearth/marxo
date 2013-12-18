package marxo.entity.report;

import com.restfb.types.Post;
import org.springframework.data.annotation.Transient;

public class FacebookRecord extends Record {
	@Transient
	public Post post;
	public long likesCount;
	public long commentsCount;
	public long sharesCount;

	public static FacebookRecord getInstance(Post post) {
		FacebookRecord facebookRecord = new FacebookRecord();
		facebookRecord.likesCount = (post.getLikes() == null) ? 0 : post.getLikes().getData().size();
		facebookRecord.sharesCount = (post.getSharesCount() == null) ? 0 : post.getSharesCount();
		facebookRecord.commentsCount = (post.getComments() == null || post.getComments().getData() == null) ? 0 : post.getComments().getData().size();
		return facebookRecord;
	}
}
