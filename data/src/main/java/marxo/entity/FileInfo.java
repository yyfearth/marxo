package marxo.entity;

import org.bson.types.ObjectId;

public class FileInfo extends BasicEntity {
	public String originalFilename;
	public String contentType;
	public Long size;

	public static FileInfo get(ObjectId objectId) {
		return mongoTemplate.findById(objectId, FileInfo.class);
	}
}
