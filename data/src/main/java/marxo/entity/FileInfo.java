package marxo.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class FileInfo extends BasicEntity {
	@JsonProperty("filename")
	public String originalFilename;
	public String contentType;
	public Long size;

	public static FileInfo get(ObjectId objectId) {
		return mongoTemplate.findById(objectId, FileInfo.class);
	}
}
