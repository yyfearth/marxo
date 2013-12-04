package marxo.controller;

import marxo.entity.report.Report;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("report{:s?}")
public class ReportController extends TenantChildController<Report> {

}
