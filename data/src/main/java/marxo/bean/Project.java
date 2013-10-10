package marxo.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Project extends Workflow {
	@JsonIgnore
	public ObjectId templateId;

	public Project() {
	}
}
