//package marxo.entity.action;
//
//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
//import com.restfb.DefaultFacebookClient;
//import com.restfb.FacebookClient;
//import com.restfb.Parameter;
//import com.restfb.exception.FacebookException;
//import com.restfb.types.FacebookType;
//import com.restfb.types.Post;
//import marxo.entity.report.FacebookRecord;
//import marxo.entity.report.Record;
//import marxo.entity.workflow.RunStatus;
//import marxo.exception.Errors;
//import marxo.exception.ValidationException;
//import org.springframework.data.annotation.Transient;
//
//@JsonIgnoreProperties(value = {"tenantDao", "contentDao"})
//public class PostFacebookAction extends MonitorableAction {
//	@Transient
//	protected FacebookContent content;
//
//	@Override
//	public FacebookContent getContent() {
//		return (content == null) ? (content = (FacebookContent) super.getContent()) : content;
//	}
//
//	@Override
//	public boolean validate(Errors errors) {
//		if (tenantId == null) {
//			errors.add(String.format("%s [%s] has no tenant", this, id));
//		}
//
//		if (getTenant().facebookData == null) {
//			errors.add(String.format("%s [%s] has no tenant", this, id));
//		}
//
//		if (contentId == null) {
//			errors.add(String.format("%s [%s] has no content", this, id));
//		}
//
//		return super.validate(errors);
//	}
//
//	private void throwIfFacebookTokenError() {
//		if (getTenant() == null) {
//			throw new ValidationException(String.format("%s has no teannt", this));
//		} else if (getTenant().facebookData == null || getTenant().facebookData.accessToken == null) {
//			throw new ValidationException(String.format("%s has no facebook token", this));
//		}
//	}
//
//	/**
//	 * todo: okay, I have to confess that this is definite a bad idea. There should be one single worker dedicated to access Facebook API. So it could controll the
//	 */
//	@Override
//	public boolean act() {
//		throwIfFacebookTokenError();
//
//		try {
//			FacebookClient facebookClient = new DefaultFacebookClient(getTenant().facebookData.accessToken);
//			getContent().publishMessageResponse = facebookClient.publish("me/feed", FacebookType.class, Parameter.with("message", getContent().message));
//			postId = content.publishMessageResponse.getId();
//			logger.debug(String.format("Submit Facebook post [%s]", content.publishMessageResponse));
//		} catch (FacebookException e) {
//			logger.debug(String.format("[%s] %s", e.getClass().getSimpleName(), e.getMessage()));
//			content.errorMessage = e.getMessage();
//			// todo: put a notification under the tenant domain.
//			return false;
//		} finally {
//			content.save();
//		}
//
//		status = RunStatus.FINISHED;
//		return true;
//	}
//
//	@Override
//	public Record monitor() {
//		throwIfFacebookTokenError();
//
//		FacebookClient facebookClient = new DefaultFacebookClient(getTenant().facebookData.accessToken);
//		Post post = facebookClient.fetchObject(postId, Post.class);
//		return new FacebookRecord(post);
//	}
//}
