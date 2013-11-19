package marxo.entity.node;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.types.FacebookType;
import marxo.dao.ContentDao;
import marxo.dao.TenantDao;
import marxo.entity.content.FacebookContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Transient;

@JsonIgnoreProperties(value = {"tenantDao", "contentDao"})
public class PostFacebook extends Action {
	@Transient
	@Autowired
	TenantDao tenantDao;
	@Transient
	@Autowired
	ContentDao contentDao;

	@Override
	public void act() {
		getTenant();
		getContent();
		FacebookClient facebookClient = new DefaultFacebookClient(tenant.facebookData.accessToken);
		FacebookContent facebookContent = (FacebookContent) content;
		facebookContent.publishMessageResponse = facebookClient.publish("me/feed", FacebookType.class, Parameter.with("message", facebookContent.message));
		saveContent();
	}

	@JsonIgnore
	public void getTenant() {
		tenant = tenantDao.findOne(this.tenantId);
	}

	@JsonIgnore
	public void getContent() {
		content = contentDao.findOne(contentId);
	}

	@JsonIgnore
	public void saveContent() {
		contentDao.save(content);
	}
}
