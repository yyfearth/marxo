package marxo.Bean;

import java.util.UUID;

public class Permission {
	UUID id;
	UUID projectId;
	String contextKey;
	boolean canRead;
	boolean canWrite;
	boolean canExecute;
}
