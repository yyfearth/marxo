//package marxo.controller;
//
//import marxo.bean.Workflow;
//import marxo.dao.WorkflowDao;
//import org.bson.types.ObjectId;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.ApplicationContext;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//import java.util.List;
//
//@Controller
//@RequestMapping("/workflow{:s?}")
//public class WorkflowController extends BasicController<Workflow, WorkflowDao> {
//	@Autowired
//	ApplicationContext applicationContext;
//	@Autowired
//	WorkflowDao workflowDao;
//
//	@RequestMapping(method = RequestMethod.GET)
//	@ResponseBody
//	public List<Workflow> getWorkflows() {
//		return workflowDao.findAll();
//	}
//
//	@RequestMapping(value = "/{id}", method = RequestMethod.POST)
//	@ResponseBody
//	public Workflow createWorkflow(@PathVariable String id) throws Exception {
//		ObjectId objectId = new ObjectId(id);
//		Workflow workflow = workflowDao.get(objectId);
//
//		if (workflow == null) {
//			throw new Exception("Oops");
//		}
//
//		return workflow;
//	}
//
//	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
//	@ResponseBody
//	public Workflow readWorkflow(@PathVariable String id) throws Exception {
//		ObjectId objectId = new ObjectId(id);
//		Workflow workflow = workflowDao.get(objectId);
//
//		if (workflow == null) {
//			throw new Exception("Oops");
//		}
//
//		return workflow;
//	}
//
//	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
//	@ResponseBody
//	public Workflow updateWorkflow(@PathVariable String id) throws Exception {
//		ObjectId objectId = new ObjectId(id);
//		Workflow workflow = workflowDao.get(objectId);
//
//		if (workflow == null) {
//			throw new Exception("Oops");
//		}
//
//		return workflow;
//	}
//
//	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
//	@ResponseBody
//	public Workflow deleteWorkflow(@PathVariable String id) throws Exception {
//		ObjectId objectId = new ObjectId(id);
//		Workflow workflow = workflowDao.get(objectId);
//
//		if (workflow == null) {
//			throw new Exception("Oops");
//		}
//
//		return workflow;
//	}
//}
