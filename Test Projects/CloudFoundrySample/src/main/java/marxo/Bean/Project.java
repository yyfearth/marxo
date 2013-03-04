package marxo.Bean;

import com.mongodb.ReflectionDBObject;

import java.util.UUID;

public class Project extends ReflectionDBObject {
	UUID id = UUID.randomUUID();
	String name = "";
}
