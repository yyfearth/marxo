package marxo.bean;

import com.github.jmkgreen.morphia.annotations.Entity;

import java.util.UUID;

@Entity(value = "nodes")
public class Node extends SharedNode {
	UUID templateId;
}
