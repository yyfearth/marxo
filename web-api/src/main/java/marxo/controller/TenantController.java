package marxo.controller;

import marxo.entity.user.Tenant;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("tenant{:s?}")
public class TenantController extends EntityController<Tenant> {
}
