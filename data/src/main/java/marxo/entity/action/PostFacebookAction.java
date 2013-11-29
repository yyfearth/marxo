package marxo.entity.action;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.exception.FacebookException;
import com.restfb.types.FacebookType;
import marxo.entity.content.FacebookContent;
import marxo.entity.workflow.RunStatus;
import org.springframework.data.annotation.Transient;

@JsonIgnoreProperties(value = {"tenantDao", "contentDao"})
public class PostFacebookAction extends FacebookAction {
	@Transient
	protected FacebookContent content;

	@Override
	public FacebookContent getContent() {
		return (content == null) ? (content = (FacebookContent) super.getContent()) : content;
	}

	@Override
	public boolean act() {
		try {
			FacebookClient facebookClient = new DefaultFacebookClient(getTenant().facebookData.accessToken);
			getContent().publishMessageResponse = facebookClient.publish("me/feed", FacebookType.class, Parameter.with("message", getContent().message));
			content.postId = content.publishMessageResponse.getId();
			logger.debug(String.format("Submit Facebook post [%s]", content.publishMessageResponse));
		} catch (FacebookException e) {
			logger.debug(String.format("[%s] %s", e.getClass().getSimpleName(), e.getMessage()));
			content.errorMessage = e.getMessage();
			// todo: put a notification under the tenant domain.pa
			return false;
		} finally {
			content.save();
		}

		status = RunStatus.FINISHED;

		return super.act();
	}
}
