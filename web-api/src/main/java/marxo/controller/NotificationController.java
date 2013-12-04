package marxo.controller;

import marxo.entity.workflow.Notification;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("notification{:s?}")
public class NotificationController extends TenantChildController<Notification> {
}
