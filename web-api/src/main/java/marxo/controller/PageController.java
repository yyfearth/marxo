package marxo.controller;

import marxo.entity.MongoDbAware;
import marxo.entity.action.Action;
import marxo.entity.action.Content;
import marxo.entity.action.Submission;
import marxo.entity.user.User;
import marxo.entity.workflow.RunStatus;
import marxo.exception.EntityNotFoundException;
import marxo.security.MarxoAuthentication;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping(value = "page{:s?}")
public class PageController implements MongoDbAware, InterceptorPreHandlable {
	protected static Sort defaultSort = new Sort(new Sort.Order(Sort.Direction.DESC, "updateTime")).and(new Sort(new Sort.Order(Sort.Direction.DESC, "createTime")));

	protected User user;
	protected Criteria criteria;

	@Override
	public void preHandle() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication != null && authentication instanceof MarxoAuthentication) {
			MarxoAuthentication marxoAuthentication = (MarxoAuthentication) authentication;
			user = marxoAuthentication.getUser();
		} else {
			user = null;
		}

		criteria = Criteria.where("type").is(Content.Type.PAGE);
	}

	@RequestMapping(value = "/{pageIdString:[\\da-fA-F]{24}}", method = RequestMethod.GET)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public Content read(@PathVariable String pageIdString) {
		Assert.isTrue(ObjectId.isValid(pageIdString));
		ObjectId pageId = new ObjectId(pageIdString);

		Query query = Query.query(criteria.and("_id").is(pageId));
		Content content = mongoTemplate.findOne(query, Content.class);

		if (content == null) {
			throw new EntityNotFoundException(Content.class, pageIdString);
		}

		Action action = content.getAction();

		if (action != null && !action.status.equals(RunStatus.FINISHED)) {
			filter(content);
		}

		return content;
	}

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public List<Content> search(@RequestParam(value = "tenant_id", required = false) ObjectId tenantId, @RequestParam(value = "project_id", required = false) ObjectId projectId) {
		criteria.and("status").is(RunStatus.STARTED);

		if (tenantId != null) {
			criteria.and("tenantId").is(tenantId);
		}

		if (projectId != null) {
			criteria.and("workflowId").is(projectId);
			criteria.and("isProject").is(true);
		}

		Query query = Query.query(criteria).with(defaultSort);
		List<Content> contents = mongoTemplate.find(query, Content.class);

		for (Content content : contents) {
			filter(content);
		}

		return contents;
	}

	@RequestMapping(value = "/{pageIdString:[\\da-fA-F]{24}}/submission{:s?}", method = RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(HttpStatus.CREATED)
	public Submission addSubmission(@PathVariable String pageIdString, @RequestBody Submission submission) throws Exception {
		Assert.isTrue(ObjectId.isValid(pageIdString));
		ObjectId pageId = new ObjectId(pageIdString);

		Assert.notNull(submission);

		submission.createUserId = submission.updateUserId = user.id;
		submission.createTime = submission.updateTime = DateTime.now();

		criteria.and("_id").is(pageId);
		Query query = Query.query(criteria);

		Update update = new Update().addToSet("submissions", submission);

		Content content = mongoTemplate.findAndModify(query, update, Content.class);
		filter(content);

		return submission;
	}

	@RequestMapping(value = "/{pageIdString:[\\da-fA-F]{24}}/submission{:s?}/mine", method = RequestMethod.GET)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public Submission getOwnSubmission(@PathVariable String pageIdString) throws Exception {
		Assert.isTrue(ObjectId.isValid(pageIdString));
		ObjectId pageId = new ObjectId(pageIdString);

		criteria.and("_id").is(pageId).and("submissions").elemMatch(
				Criteria.where("createUserId").is(user.id)
		);
		Query query = Query.query(criteria);
		query.fields().include("submissions");

		Content content = mongoTemplate.findOne(query, Content.class);

		if (content == null || content.submissions.size() < 1) {
			throw new EntityNotFoundException(Content.class, pageId);
		}

		return content.submissions.get(0);
	}

	protected void filter(Content content) {
		content.records = null;
		content.submissions = null;
	}
}
