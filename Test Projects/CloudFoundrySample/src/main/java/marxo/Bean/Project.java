package marxo.Bean;

import com.github.jmkgreen.morphia.annotations.*;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.UUID;

@Entity
public class Project {

	@Id
	public ObjectId _id;

	public UUID id = UUID.randomUUID();

	public String name = "";

	public UUID tenantId;

	public UUID workflowId;

	String contextKey;

	ProjectType type = ProjectType.None;

	ProjectStatus status = ProjectStatus.None;

	public UUID createdUserId;

	public UUID lastModifiedUserId;

	@JsonProperty
	public Date createdDateTime = new Date();

	public Date lastModifiedDateTime = new Date();

	@PrePersist
	public void prePersist() {
//		createdTick = createdDateTime.getMillis();
//		lastModifiedTick = lastModifiedDateTime.getMillis();
	}

	@PostLoad
	public void postLoad() {
//		createdDateTime = new DateTime(createdTick);
//		lastModifiedDateTime = new DateTime(lastModifiedTick);
	}
}
