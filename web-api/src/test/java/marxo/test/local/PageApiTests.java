package marxo.test.local;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.net.MediaType;
import marxo.entity.action.Action;
import marxo.entity.action.Content;
import marxo.entity.action.Submission;
import marxo.entity.node.Node;
import marxo.entity.workflow.Workflow;
import marxo.test.ApiTestConfiguration;
import marxo.test.ApiTester;
import marxo.test.BasicApiTests;
import org.apache.http.Header;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

@ApiTestConfiguration("http://localhost:8080/api/page/")
public class PageApiTests extends BasicApiTests {

	Workflow reusedWorkflow;
	Node reusedNode;
	Action reusedAction;
	Content reusedContent;
	Submission reusedSubmission;

	@BeforeClass
	@Override
	public void beforeClass() throws Exception {
		super.beforeClass();

		reusedWorkflow = new Workflow();
		reusedWorkflow.setTenant(reusedTenant);

		reusedNode = new Node();
		reusedWorkflow.addNode(reusedNode);

		reusedAction = new Action(Action.Type.PAGE);
		reusedNode.addAction(reusedAction);

		reusedContent = new Content(Content.Type.PAGE);
		reusedAction.setContent(reusedContent);

		insertEntities(
				reusedWorkflow,
				reusedNode,
				reusedAction,
				reusedContent
		);
	}

	@Test
	public void readPage() throws Exception {
		try (ApiTester apiTester = apiTesterBuilder.build()) {
			apiTester
					.httpGet(reusedContent.id.toString())
					.send();
			apiTester
					.isOk()
					.matchContentType(MediaType.JSON_UTF_8);
			Content content = apiTester.getContent(Content.class);
			Assert.assertNotNull(content);
			Assert.assertEquals(content.id, reusedContent.id);
			Assert.assertEquals(content.records.size(), 0);
			Assert.assertEquals(content.submissions.size(), 0);
		}
	}

	@Test
	public void searchPages() throws Exception {
		try (ApiTester apiTester = apiTesterBuilder.build()) {
			apiTester
					.httpGet(String.format("?tenant_id=%s", reusedTenant.id))
					.send();
			apiTester
					.isOk()
					.matchContentType(MediaType.JSON_UTF_8);
			List<Content> contents = apiTester.getContent(new TypeReference<List<Content>>() {
			});
			Assert.assertEquals(contents.size(), 0);
		}
	}

	@Test
	public void createSubmission() throws Exception {
		reusedSubmission = new Submission();

		try (ApiTester apiTester = apiTesterBuilder.build()) {
			apiTester
					.httpPost(String.format("%s/submission", reusedContent.id), reusedSubmission)
					.send();
			apiTester
					.isCreated()
					.matchContentType(MediaType.JSON_UTF_8);
			Header header = apiTester.getHeader("Location");
			// todo: test the header

			Submission submission = apiTester.getContent(Submission.class);
			Assert.assertNotNull(submission);
			Assert.assertEquals(reusedSubmission.id, submission.id);
		}
	}

	@Test(dependsOnMethods = "createSubmission")
	public void getOwnSubmission() throws Exception {
		try (ApiTester apiTester = apiTesterBuilder.build()) {
			apiTester
					.httpGet(String.format("%s/submission/mine", reusedContent.id))
					.send();
			apiTester
					.isOk()
					.matchContentType(MediaType.JSON_UTF_8);

			Submission submission = apiTester.getContent(Submission.class);
			Assert.assertNotNull(submission);
			Assert.assertEquals(reusedSubmission.id, submission.id);
		}
	}
}
