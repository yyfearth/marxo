package marxo.entity.node;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.exception.FacebookException;
import com.restfb.types.FacebookType;
import marxo.entity.content.FacebookContent;
import org.springframework.data.annotation.Transient;

@JsonIgnoreProperties(value = {"tenantDao", "contentDao"})
public class PostFacebook extends Action {
	@Transient
	protected FacebookContent content;

	@Override
	public FacebookContent getContent() {
		return content = (FacebookContent) super.getContent();
	}

	@Override
	public boolean act() {
		if (getTenant() == null) {
			logger.error(String.format("Action [%s] has no tenant", id));
			return false;
		}

		if (getTenant().facebookData == null) {
			logger.debug(String.format("Tenant [%s] has no facebook info", getTenant().id));
			return false;
		}

		try {
			FacebookClient facebookClient = new DefaultFacebookClient();
			content.publishMessageResponse = facebookClient.publish("me/feed", FacebookType.class, Parameter.with("message", content.message));
		} catch (FacebookException e) {
			logger.debug(String.format("[%s] %s", e.getClass().getSimpleName(), e.getMessage()));
			// todo: save the error to content.
			// todo: put a notification under the tenant domain.
		}

		return true;
	}
}
