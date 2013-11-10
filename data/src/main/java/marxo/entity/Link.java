package marxo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Link extends WorkflowChildEntity {
	public ObjectId previousNodeId;
	public ObjectId nextNodeId;
	public Condition condition;
}
