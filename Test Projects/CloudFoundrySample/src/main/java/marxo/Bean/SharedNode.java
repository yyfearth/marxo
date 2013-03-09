package marxo.Bean;

import java.util.List;
import java.util.UUID;

public class SharedNode {
	UUID id;
	String name;
	UUID workflowId;
	List<SharedAction> sharedActions;
}
