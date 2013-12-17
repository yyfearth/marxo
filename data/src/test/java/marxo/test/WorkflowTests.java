package marxo.test;

import com.google.common.collect.Lists;
import marxo.entity.BasicEntity;
import marxo.entity.link.Link;
import marxo.entity.node.Node;
import marxo.entity.user.Tenant;
import marxo.entity.workflow.Workflow;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings({"uncheck", "unchecked"})
public class WorkflowTests extends BasicDataTests {

	@Test
	public void wireWorkflow() throws Exception {
		Tenant tenant = new Tenant();

		Workflow workflow = new Workflow();
		workflow.setTenant(tenant);

		Node node1 = new Node();
		Node node2 = new Node();
		Node node3 = new Node();

		workflow.setNodes(Lists.newArrayList(node1, node2, node3));

		Link link1 = new Link();
		link1.previousNodeId = node1.id;
		link1.nextNodeId = node2.id;

		Link link2 = new Link();
		link2.previousNodeId = node2.id;
		link2.nextNodeId = node3.id;

		workflow.setLinks(Lists.newArrayList(link1, link2));

		workflow.wire();

		Assert.assertEquals(workflow.getStartNode().id, node1.id);

		Assert.assertTrue(node1.getFromLinkIds().isEmpty());
		Assert.assertEquals(node1.getToLinkIds(), Arrays.asList(link1.id));
		Assert.assertEquals(node1.getToNodeIds(), Arrays.asList(node2.id));

		Assert.assertEquals(node2.getFromLinkIds(), Arrays.asList(link1.id));
		Assert.assertEquals(node2.getToLinkIds(), Arrays.asList(link2.id));
		Assert.assertEquals(node2.getToNodeIds(), Arrays.asList(node3.id));

		Assert.assertEquals(node3.getFromLinkIds(), Arrays.asList(link2.id));
		Assert.assertTrue(node3.getToLinkIds().isEmpty());
		Assert.assertTrue(node3.getToNodeIds().isEmpty());

		for (Node node : Lists.newArrayList(node1, node2, node3)) {
			Assert.assertEquals(node.tenantId, tenant.id);
		}

		for (Link link : Lists.newArrayList(link1, link2)) {
			Assert.assertEquals(link.tenantId, tenant.id);
		}
	}

//	@Test
	public void deepWireWorkflow() throws Exception {
		List<BasicEntity> entitiesToSave = new ArrayList<>();

		Tenant tenant = new Tenant();
		entitiesToSave.add(tenant);

		Workflow workflow = new Workflow();
		workflow.setTenant(tenant);
		entitiesToSave.add(workflow);

		Node node1 = new Node();
		Node node2 = new Node();
		Node node3 = new Node();

		workflow.setNodes(Lists.newArrayList(node1, node2, node3));
		entitiesToSave.addAll(Lists.newArrayList(node1, node2, node3));

		Link link1 = new Link();
		link1.previousNodeId = node1.id;
		link1.nextNodeId = node2.id;

		Link link2 = new Link();
		link2.previousNodeId = node2.id;
		link2.nextNodeId = node3.id;

		workflow.setLinks(Lists.newArrayList(link1, link2));
		entitiesToSave.addAll(Lists.newArrayList(link1, link2));

		mongoTemplate.insertAll(entitiesToSave);
		entitiesToRemove.addAll(entitiesToSave);
		workflow.deepWire();

		workflow = Workflow.get(workflow.id);
		Assert.assertEquals(workflow.getStartNode().id, node1.id);

		node1 = Node.get(node1.id);
		node2 = Node.get(node2.id);
		node3 = Node.get(node3.id);
		link1 = Link.get(link1.id);
		link2 = Link.get(link2.id);

		Assert.assertTrue(node1.getFromLinkIds().isEmpty());
		Assert.assertEquals(node1.getToLinkIds(), Arrays.asList(link1.id));
		Assert.assertEquals(node1.getToNodeIds(), Arrays.asList(node2.id));

		Assert.assertEquals(node2.getFromLinkIds(), Arrays.asList(link1.id));
		Assert.assertEquals(node2.getToLinkIds(), Arrays.asList(link2.id));
		Assert.assertEquals(node2.getToNodeIds(), Arrays.asList(node3.id));

		Assert.assertEquals(node3.getFromLinkIds(), Arrays.asList(link2.id));
		Assert.assertTrue(node3.getToLinkIds().isEmpty());
		Assert.assertTrue(node3.getToNodeIds().isEmpty());

		for (Node node : Lists.newArrayList(node1, node2, node3)) {
			Assert.assertEquals(node.tenantId, tenant.id);
		}

		for (Link link : Lists.newArrayList(link1, link2)) {
			Assert.assertEquals(link.tenantId, tenant.id);
		}
	}
}
