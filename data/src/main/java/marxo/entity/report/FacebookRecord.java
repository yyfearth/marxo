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
		facebookRecord.likesCount = (post.getLikesCount() == null) ? 0 : post.getLikesCount();
		facebookRecord.sharesCount = (post.getSharesCount() == null) ? 0 : post.getSharesCount();
		facebookRecord.commentsCount = (post.getComments() == null || post.getComments().getCount() == null) ? 0 : post.getComments().getCount();
		return facebookRecord;
	}
}
