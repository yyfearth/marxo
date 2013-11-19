package marxo.controller;

import marxo.dao.NotificationDao;
import marxo.entity.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("notification{:s?}")
public class NotificationController extends TenantChildController<Notification> {
	@Autowired
	protected NotificationController(NotificationDao dao) {
		super(dao);
	}
}
