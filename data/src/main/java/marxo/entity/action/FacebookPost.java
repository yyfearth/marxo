package marxo.entity.action;

import com.fasterxml.jackson.annotation.JsonIgnore;
import marxo.entity.Content;

public class FacebookPost extends Content {
	public String content;
	@JsonIgnore
	public String parsedContent;
}
