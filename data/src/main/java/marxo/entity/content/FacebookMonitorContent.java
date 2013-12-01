package marxo.entity.content;

import com.restfb.types.Post;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class FacebookMonitorContent extends Content {
	public String postId;
	public List<FacebookRecord> records = new ArrayList<>();
	public List<Post> posts = new ArrayList<>();

	public static class FacebookRecord {
		public DateTime time;
		public int linkCount = 0;
		public int commentCount = 0;
	}
}