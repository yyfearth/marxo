package marxo.entity.node;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.types.FacebookType;
import marxo.entity.content.Content;
import marxo.entity.content.FacebookContent;
import marxo.entity.user.Tenant;

@JsonIgnoreProperties(value = {"tenantDao", "contentDao"})
public class PostFacebook extends Action {

	@Override
	public void act() {
		getContent();
		FacebookClient facebookClient = new DefaultFacebookClient(getTenant().facebookData.accessToken);
		FacebookContent facebookContent = (FacebookContent) getContent();
		facebookContent.publishMessageResponse = facebookClient.publish("me/feed", FacebookType.class, Parameter.with("message", facebookContent.message));
	}

	@JsonIgnore
	@Override
	public Tenant getTenant() {
		return (tenant == null) ? (tenant = mongoTemplate.findById(this.tenantId, Tenant.class)) : tenant;
	}

	@JsonIgnore
	public Content getContent() {
		return (content == null) ? (content = mongoTemplate.findById(this.contentId, Content.class)) : content;
	}
}
