package marxo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class FacebookPost extends Content {
	public String content;
	@JsonIgnore
	public String parsedContent;
}
