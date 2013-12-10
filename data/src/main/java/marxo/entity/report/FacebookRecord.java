package marxo.entity.report;

import com.restfb.types.Post;
import org.springframework.data.annotation.Transient;

public class FacebookRecord extends Record {
	@Transient
	public Post post;
	public long likesCount;
	public long commentsCount;
	public long sharesCount;

	public FacebookRecord(Post post) {
		this.post = post;
		likesCount = post.getLikesCount();
		commentsCount = post.getComments().getCount();
		sharesCount = post.getSharesCount();
	}
}
