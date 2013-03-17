package marxo.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.jmkgreen.morphia.annotations.Entity;
import org.bson.types.ObjectId;

@Entity(value = "workflows")
public class Project extends Workflow {
	public ObjectId getTemplateId() {
		return templateId;
	}

	public void setTemplateId(ObjectId templateId) {
		this.templateId = templateId;
	}

	@JsonIgnore
	ObjectId templateId;

	@JsonProperty("template")
	public Node getTemplate() {
		return (templateId == null) ? null : new Node() {{
			id = templateId;
		}};
	}

	@JsonProperty("template")
	public void setTemplate(Node template) {
		templateId = (template == null) ? null : template.id;
	}
}
