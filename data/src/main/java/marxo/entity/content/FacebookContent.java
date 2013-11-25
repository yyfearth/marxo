package marxo.entity.content;

import com.restfb.types.FacebookType;
import org.springframework.data.mongodb.core.mapping.Document;

public class FacebookContent extends Content {
	public String message;
	public FacebookType publishMessageResponse;
	public String postId;
}
