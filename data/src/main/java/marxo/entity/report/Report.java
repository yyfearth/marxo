//package marxo.entity.report;
//
//import marxo.entity.action.ActionChildEntity;
//import marxo.entity.action.MonitorableAction;
//import org.bson.types.ObjectId;
//import org.springframework.data.mongodb.core.mapping.Document;
//import sun.reflect.generics.reflectiveObjects.NotImplementedException;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Document(collection = "report")
//public class Report extends ActionChildEntity {
//
//	public Map<ObjectId, List<Record>> records = new HashMap<>();
//
//	public static Report get(ObjectId objectId) {
//		return mongoTemplate.findById(objectId, Report.class);
//	}
//
//	public static Report addRecord(Report report, MonitorableAction monitorableAction, Record record) {
//		throw new NotImplementedException();
//	}
//}
