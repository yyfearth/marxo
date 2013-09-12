package marxo.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Project extends Workflow {
	@JsonIgnore
	ObjectId templateId;

	public Project() {
	}

	public Project(ObjectId templateId) {

	}

	public ObjectId getTemplateId() {
		return templateId;
	}

	public void setTemplateId(ObjectId templateId) {
		this.templateId = templateId;
	}


//	@JsonProperty("template")
//	public Node getTemplate() {
//		return (templateId == null) ? null : new Node() {{
//			id = templateId;
//		}};
//	}
//
//	@JsonProperty("template")
//	public void setTemplate(Node template) {
//		templateId = (template == null) ? null : template.id;
//	}
}
