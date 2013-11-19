package marxo.dao;

import marxo.entity.link.Link;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

@Repository
public class LinkDao extends WorkflowChildDao<Link> {
}
