package marxo.bean;

import java.util.UUID;

public class Link extends BasicEntity {
	String name;
	UUID previoudNodeId;
	UUID nextNodeId;
	Condition condition;
}
