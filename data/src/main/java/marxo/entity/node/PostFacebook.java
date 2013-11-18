package marxo.entity.node;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.types.FacebookType;
import marxo.dao.ContentDao;
import marxo.dao.TenantDao;
import marxo.entity.content.FacebookContent;

public class PostFacebook extends Action {
	TenantDao tenantDao = new TenantDao();
	ContentDao contentDao = new ContentDao();

	@Override
	public void act() {
		getTenant();
		getContent();
		FacebookClient facebookClient = new DefaultFacebookClient(tenant.facebookData.accessToken);
		FacebookContent facebookContent = (FacebookContent) content;
		facebookContent.publishMessageResponse = facebookClient.publish("me/feed", FacebookType.class, Parameter.with("message", facebookContent.message));
		saveContent();
	}

	public void getTenant() {
		tenant = tenantDao.findOne(this.tenantId);
	}

	public void getContent() {
		content = contentDao.findOne(contentId);
	}

	public void saveContent() {
		contentDao.save(content);
	}
}
