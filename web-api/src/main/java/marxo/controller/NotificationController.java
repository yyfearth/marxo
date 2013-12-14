package marxo.controller;

import marxo.entity.workflow.Notification;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("notification{:s?}")
public class NotificationController extends TenantChildController<Notification> {

	@Autowired
	HttpServletRequest request;

	@Override
	protected Criteria newDefaultCriteria() {
		Criteria criteria = Criteria
				.where("tenantId").is(user.tenantId)
				.and("expireTime").gt(DateTime.now());

		return criteria;
	}

	@Override
	protected Criteria newDefaultCriteria(ObjectId id) {
		return newDefaultCriteria().and("_id").is(id);
	}

	@Override
	public List<Notification> search() {

		Criteria criteria = newDefaultCriteria();

		List<Notification> notifications = mongoTemplate.find(new Query(criteria).with(getDefaultSort()), entityClass);
		Collections.sort(notifications);    // Sort with level.

		return notifications;
	}
}
